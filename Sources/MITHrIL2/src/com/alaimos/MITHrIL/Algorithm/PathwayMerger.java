package com.alaimos.MITHrIL.Algorithm;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Factory.PathwayFactory;
import com.alaimos.MITHrIL.Data.Pathway.Impl.MergedRepository;
import com.alaimos.MITHrIL.Data.Pathway.Interface.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This algorithm build a meta-pathway using all the pathways in a repository
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 27/12/2015
 */
public class PathwayMerger extends AbstractAlgorithm<MergedRepository> {

    public PathwayMerger() {
    }

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<MergedRepository> init() {
        super.init();
        return this;
    }

    @NotNull
    private Predicate<NodeInterface> getFilterPredicate(Pattern[] patterns) {
        if (patterns == null || patterns.length == 0) return n -> true;
        return n -> {
            for (Pattern p : patterns) {
                if (p.matcher(n.getId()).matches()) {
                    return false;
                }
            }
            return true;
        };
    }

    private static Stream<PathwayInterface> buildPathwayStream(RepositoryInterface r, String[] includeCategories, String[] excludeCategories,
                                                               String[] includePathways, String[] excludePathways) {
        Stream<PathwayInterface> pathwayStream; //A stream of pathways to merge
        List<PathwayInterface> pathways;        // List of pathways to merge
        if (includeCategories == null || includeCategories.length == 0) { // if no category are selected
            pathways = r.getPathways();                                   // then selects all categories
        } else {                                                          // otherwise it uses user selection
            pathways = r.getPathwaysByCategory(Arrays.asList(includeCategories));
        }
        if (includePathways != null && includePathways.length > 0) {
            pathwayStream = pathways.stream().filter(p -> ArrayUtils.contains(includePathways, p.getId()));
        } else {
            pathwayStream = pathways.stream();
        }
        if (excludeCategories != null && excludeCategories.length > 0) {  // if there are excluded categories
            List<String> exclude = r.getPathwayIdsByCategory(Arrays.asList(excludeCategories));
            pathwayStream = pathwayStream.filter(p -> (!exclude.contains(p.getId())));
        }
        if (excludePathways != null && excludePathways.length > 0) {
            pathwayStream = pathwayStream.filter(p -> (!ArrayUtils.contains(excludePathways, p.getId())));
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
        pathwayStream.filter(PathwayInterface::hasGraph).forEach(p -> {
            notifyObservers("mergingPathway", p);
            ArrayList<Pair<String, String>> edges = new ArrayList<>();
            GraphInterface go = p.getGraph();
            GraphInterface gn = metaPathway.getGraph();
            notifyObservers("mergingPathwayNodes", p);
            go.getNodes().forEach((s, n) -> { //For each node in "p"
                if (!gn.hasNode(s) && filterPredicate.test(n)) { //if it does not exists
                    gn.addNode((NodeInterface) n.clone());       //then it will be copied
                }
            });
            notifyObservers("mergingPathwayEdges", p);
            go.getEdgesStream().forEach(edge -> { //For each edge in "p"
                String start = edge.getStart().getId(), end = edge.getEnd().getId();
                EdgeInterface ne;
                boolean hasEdge = false;
                if (gn.hasNode(start) && gn.hasNode(end)) {
                    //A copy of the list of descriptions for the edge
                    ArrayList<EdgeDescriptionInterface> ds = new ArrayList<>(edge.getDescriptions());
                    if (gn.hasEdge(start, end)) { //If the edge exists in the meta-pathway
                        ne = gn.getEdge(start, end); //Get the edge from the meta-pathway
                        ds.addAll(ne.getDescriptions()); //and adds its description to "ds"
                        hasEdge = true; //We won't need to add the edge to the graph
                    } else { //Otherwise
                        ne = pf.getEdge(gn.getNode(start), gn.getNode(end)); //it creates a new edge
                    }
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
