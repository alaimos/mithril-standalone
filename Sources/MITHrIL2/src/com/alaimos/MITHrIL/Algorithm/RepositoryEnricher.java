package com.alaimos.MITHrIL.Algorithm;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.MITHrIL.Data.Pathway.Factory.PathwayFactory;
import com.alaimos.MITHrIL.Data.Pathway.Interface.*;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.NodeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;

import java.util.stream.Stream;

/**
 * This algorithmClass merges all the pathways in a repository in a single graph
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 27/12/2015
 */
public class RepositoryEnricher extends AbstractAlgorithm<RepositoryInterface> {

    public RepositoryEnricher() {
    }

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<RepositoryInterface> init() {
        super.init();
        return this;
    }

    /**
     * Merges all the pathways in a repository building a new repository with a single pathway graph
     */
    @Override
    public void run() {
        var r = getParameterNotNull("repository", RepositoryInterface.class);
        var e = getParameterNotNull("enrichment", RepositoryEnrichmentInterface.class);
        var pf = getOptionalParameter("factory", PathwayFactoryInterface.class).orElse(PathwayFactory.getInstance());
        output = (RepositoryInterface) r.clone();
        notifyObservers("enrichmentStart");
        for (EdgeEnrichmentInterface ee : e.getAdditionalEdges()) {
            notifyObservers("startEnrichingWithEdge", ee);
            NodeEnrichmentInterface start = ee.getStart(), end = ee.getEnd();
            Stream<PathwayInterface> ps = null;
            if (start.isMustExist()) {
                ps = output.getPathwaysByNodeId(start.getId()).stream().map(output::getPathwayById).filter(PathwayInterface::hasGraph);
            } else if (end.isMustExist()) {
                ps = output.getPathwaysByNodeId(end.getId()).stream().map(output::getPathwayById).filter(PathwayInterface::hasGraph);
            } else if (start.isMustExist() && end.isMustExist()) {
                ps = output.getPathwaysByNodeId(start.getId()).stream().map(output::getPathwayById)
                           .filter(p -> p.hasGraph() && p.getGraph().hasNode(end.getId()));
            } else if (!start.isMustExist() && !end.isMustExist()) {
                ps = output.stream();
            }
            if (ps != null) {
                ps.forEach(p -> {
                    notifyObservers("startEnrichingPathway", p);
                    GraphInterface g = p.getGraph();
                    NodeInterface n1 = (g.hasNode(start.getId())) ? g.getNode(start.getId()) : null,
                            n2 = (g.hasNode(end.getId())) ? g.getNode(end.getId()) : null;
                    if (n1 != null && n2 != null && g.hasEdge(n1, n2)) return;
                    if (n1 == null) {
                        n1 = (pf.getNode(start.getId(), start.getName(), start.getType())).addAliases(start.getAliases());
                    }
                    if (n2 == null) {
                        n2 = (pf.getNode(end.getId(), end.getName(), end.getType())).addAliases(end.getAliases());
                    }
                    EdgeInterface edge = pf.getEdge(n1, n2);
                    ee.getDescriptions().forEach(ed -> edge.addDescription(ed.getType(), ed.getSubType(), p));
                    g.addEdge(edge);
                    notifyObservers("doneEnrichingPathway", p);
                });
            }
            notifyObservers("doneEnrichingWithEdge", ee);
        }
        output.forEach(p -> p.setName(p.getName() + " - Enriched"));
        notifyObservers("enrichmentDone");
    }
}
