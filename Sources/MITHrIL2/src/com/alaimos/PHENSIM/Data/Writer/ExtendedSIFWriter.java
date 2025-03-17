package com.alaimos.PHENSIM.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Results.PhensimRun;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class ExtendedSIFWriter extends AbstractDataWriter<PhensimRun> {

    protected RepositoryInterface r;
    private final Map<String, Double> absoluteWeights = new HashMap<>();
    private String pathway = null;
    private GraphInterface graph = null;

    public ExtendedSIFWriter(RepositoryInterface r) {
        this.r = r;
    }

    /**
     * Clean-up the node name.
     *
     * @param node A node
     * @return A cleaned up name
     */
    public String cleanNodeName(@NotNull NodeInterface node) {
        var name = node.getName();
        var parts = name.split(",\\s*");
        if (parts.length <= 0) return name;
        return parts[0];
    }

    /**
     * Compute the total weight of a given node. The total weight is obtained by
     * summing up the absolute weight values for all outgoing edges.
     *
     * @param u The node for which the total weight is computed
     * @return the total weight
     */
    private double absoluteTotalWeight(String u) {
        try {
            if (absoluteWeights.containsKey(u)) return absoluteWeights.get(u);
            var start = graph.getNode(u);
            double weight = 0.0;
            for (NodeInterface d : graph.outgoingNodes(start)) {
                var w = Math.abs(graph.getEdge(start, d).computeWeight());
                weight += w;
            }
            absoluteWeights.put(u, weight);
            return weight;
        } catch (NullPointerException ignore) {
        }
        return Double.NaN;
    }

    /**
     * Write all edges for a pathway
     */
    public void writeEdges(PrintStream ps, PhensimRun data) {
        var i = 0;
        for (var edges : graph.getEdges().values()) {
            for (var edge : edges.values()) {
                var start = edge.getStart();
                var end = edge.getEnd();
                var weight = edge.computeWeight() / absoluteTotalWeight(start.getId());
                if (Double.isFinite(weight) && weight != 0) {
                    var startId = start.getId();
                    var endId = end.getId();
                    // format: start end weight pathway_id start_name end_name start_as start_pert start_pv start_fdr end_as end_pert end_pv end_fdr
                    writeArray(ps, new String[]{
                            startId,
                            endId,
                            Double.toString(weight),
                            pathway,
                            cleanNodeName(start),
                            Double.toString(data.getActivityScore(pathway, startId)),
                            Double.toString(data.getAveragePerturbation(pathway, startId)),
                            Double.toString(data.getNodePValue(pathway, startId)),
                            Double.toString(data.getNodeAdjustedPValue(pathway, startId)),
                            cleanNodeName(end),
                            Double.toString(data.getActivityScore(pathway, endId)),
                            Double.toString(data.getAveragePerturbation(pathway, endId)),
                            Double.toString(data.getNodePValue(pathway, endId)),
                            Double.toString(data.getNodeAdjustedPValue(pathway, endId))
                    });
                    ps.println();
                }
            }
        }
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<PhensimRun> write(@NotNull PhensimRun data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            for (var p : r) {
                if (p.hasGraph()) {
                    pathway = p.getId();
                    graph = p.getGraph();
                    writeArray(ps, new String[]{
                            "# source",
                            "target",
                            "edge_weight",
                            "edge_source",
                            "source_name",
                            "source_activity_score",
                            "source_perturbation",
                            "source_pvalue",
                            "source_fdr",
                            "target_name",
                            "target_activity_score",
                            "target_perturbation",
                            "target_pvalue",
                            "target_fdr"
                    });
                    ps.println();
                    writeEdges(ps, data);
                }
            }
        }
        return this;
    }
}
