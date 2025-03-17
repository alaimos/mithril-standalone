package com.alaimos.PHENSIM.Algorithm;

import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Factory.PathwayFactory;
import com.alaimos.MITHrIL.Data.Pathway.Impl.MergedRepository;
import com.alaimos.MITHrIL.Data.Pathway.Interface.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This algorithm build a meta-pathway using all the pathways in a repository
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class PathwayMerger extends com.alaimos.MITHrIL.Algorithm.PathwayMerger {

    protected Stream<PathwayInterface> buildPathwayStream(RepositoryInterface r, String[] includeCategories, String[] excludeCategories,
                                                          String[] includePathways, String[] excludePathways) {
        var pathwayStream = super.buildPathwayStream(r, includeCategories, excludeCategories,
                includePathways, excludePathways);
        pathwayStream = pathwayStream.filter(p -> !p.hasCategory("reactome"));
        var reactomePathways = r.getPathwaysByCategory("reactome");
        if (reactomePathways != null) {
            pathwayStream = Stream.concat(pathwayStream, reactomePathways.stream());
        }
        return pathwayStream;
    }

    /**
     * Merges all the pathways in a repository building a new repository with a single pathway graph
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        var r = getParameterNotNull("repository", RepositoryInterface.class);
        var includeCategories = getParameter("include", String[].class);
        var excludeCategories = getParameter("exclude", String[].class);
        var includePathways = getParameter("includePathways", String[].class);
        var excludePathways = getParameter("excludePathways", String[].class);
        var nodesFilters = getParameter("nodesFilter", Pattern[].class);
        var disablePriority = getOptionalParameter("disablePriority", Boolean.class).orElse(false);
        var filterPredicate = getFilterPredicate(nodesFilters);
        var pf = getOptionalParameter("factory", PathwayFactoryInterface.class).orElse(PathwayFactory.getInstance());

        var metaPathway = pf.getPathway("metap", "Meta-Pathway", pf.getGraph()).setHidden(true);
        var originalToMeta = new HashMap<String, List<Pair<String, String>>>();

        notifyObservers("startPathwayMerging");
        var pathwayStream = buildPathwayStream(r, includeCategories, excludeCategories, includePathways, excludePathways);
        var endpoints = new HashSet<String>(); // The list of endpoints in the meta-pathway
        metaPathway.setDefaultWeightComputation();
        r.setDefaultWeightComputation();
        pathwayStream.filter(PathwayInterface::hasGraph).forEach(p -> {
            notifyObservers("mergingPathway", p);
            var edges = new ArrayList<Pair<String, String>>();
            var go = p.getGraph();
            var gn = metaPathway.getGraph();
            notifyObservers("mergingPathwayNodes", p);
            go.getNodes().forEach((s, n) -> { //For each node in "p"
                if (!gn.hasNode(s) && filterPredicate.test(n)) { //if it does not exists
                    gn.addNode((NodeInterface) n.clone());       //then it will be copied
                }
            });
            notifyObservers("mergingPathwayEdges", p);
            var reactome = p.hasCategory("reactome");
            go.getEdgesStream().forEach(edge -> { //For each edge in "p"
                String start = edge.getStart().getId(), end = edge.getEnd().getId();
                EdgeInterface ne;
                var hasEdge = false;
                if (gn.hasNode(start) && gn.hasNode(end)) {
                    //A copy of the list of descriptions for the edge
                    var ds = new ArrayList<>(edge.getDescriptions());
                    if (gn.hasEdge(start, end)) { //If the edge exists in the meta-pathway
                        ne = gn.getEdge(start, end); //Get the edge from the meta-pathway
                        ds.addAll(ne.getDescriptions()); //and adds its description to "ds"
                        hasEdge = true; //We won't need to add the edge to the graph
                    } else { //Otherwise
                        ne = pf.getEdge(gn.getNode(start), gn.getNode(end)); //it creates a new edge
                    }
                    var w1 = hasEdge ? gn.getEdge(start, end).computeWeight() : 0.0;
                    var w2 = hasEdge ? edge.computeWeight() : 0.0;
                    if (!reactome || !hasEdge || (w1 == 0 && w1 != w2)) { // The edge will be modified only if it does not exist in the meta-pathway or we are not in reactome
                        if (!disablePriority) {
                            //The new edge will have all the descriptions with maximal priority taken from "ds"
                            //So that multi-edge with missing interactions will be handled correctly
                            ne.clearDescriptions();
                            int maxPriority = ds.stream().mapToInt(d -> d.getSubType().priority()).max().orElse(0);
                            ds.stream().filter(d -> d.getSubType().priority() == maxPriority)
                                    .forEach(d -> ne.addDescription(((EdgeDescriptionInterface) d.clone()).setOwner(metaPathway)));
                        } else {
                            ds.forEach(d -> ne.addDescription(((EdgeDescriptionInterface) d.clone()).setOwner(metaPathway)));
                        }
                        //Adds the new edge
                        if (ne.getDescriptions().size() > 0) {
                            if (!hasEdge) gn.addEdge(ne);
                            edges.add(new Pair<>(start, end)); //Remembers the original pathway
                        }
                    } else {
                        edges.add(new Pair<>(start, end)); //Remembers the original pathway
                    }
                }
            });
            endpoints.addAll(go.getEndpoints());
            originalToMeta.put(p.getId(), edges);
            notifyObservers("doneMergingPathway", p);
        });
        metaPathway.getGraph().setEndpoints(new ArrayList<>(endpoints));
        for (String v : r.getVirtualPathways()) {
            List<Pair<String, String>> edges = r.getEdgesByVirtualPathway(v);
            originalToMeta.put(v, edges.stream().map(p -> (Pair<String, String>) p.clone()).collect(Collectors.toList()));
        }
        output = new MergedRepository(metaPathway, originalToMeta);
        for (var k : originalToMeta.keySet()) {
            output.setNameOfVirtualPathway(k, r.getPathwayById(k).getName());
        }
        notifyObservers("donePathwayMerging");
    }
}
