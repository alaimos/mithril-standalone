package com.alaimos.PHENSIM.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Results.PhensimRun;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PhensimRunWriter extends AbstractDataWriter<PhensimRun> {

    protected RepositoryInterface r;
    private final Double significanceThreshold;

    public PhensimRunWriter(Double significanceThreshold) {
        this.significanceThreshold = significanceThreshold;
    }

    private void writeLine(PrintStream ps, String pathwayId, String pathwayName, String nodeId, GraphInterface sourceGraph,
                           @NotNull PhensimRun results, List<String> nodesFilter) {
        var as = results.getActivityScore(pathwayId, nodeId);
        if (significanceThreshold == null || Math.abs(as) > significanceThreshold) {
            var ds = results.getDirectlyTargetedBy(pathwayId);
            var targets = "";
            if (ds.containsKey(nodeId)) {
                var tmp = ds.get(nodeId).stream();
                if (nodesFilter != null) {
                    tmp = tmp.filter(nodesFilter::contains);
                }
                targets = tmp.collect(Collectors.joining(","));
            }
            pathwayName = pathwayName.replaceAll("\\s+-\\s+Enriched", "");
            writeArray(ps, new String[]{
                    pathwayId,
                    pathwayName,
                    nodeId,
                    sourceGraph.getNode(nodeId).getName(),
                    (sourceGraph.getEndpoints().contains(nodeId)) ? "Yes" : "No",
                    (results.getDirectTargets(pathwayId).contains(nodeId)) ? "Yes" : "No",
                    Double.toString(as),
                    Double.toString(results.getNodePValue(pathwayId, nodeId)),
                    Double.toString(results.getNodeAdjustedPValue(pathwayId, nodeId)),
                    concatArray(results.getLogProbabilities(pathwayId, nodeId), ","),
                    Double.toString(results.getPathwayActivityScore(pathwayId)),
                    Double.toString(results.getPathwayPValue(pathwayId)),
                    Double.toString(results.getPathwayAdjustedPValue(pathwayId)),
                    concatArray(results.getPathwayLogProbabilities(pathwayId), ","),
                    targets,
                    Double.toString(results.getAveragePerturbation(pathwayId, nodeId)),
                    Double.toString(results.getAveragePathwayPerturbation(pathwayId))
            });
            ps.println();
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
        var r = data.getRepository();
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println(concatArray(new String[]{
                    "# Pathway Id",
                    "Pathway Name",
                    "Node Id",
                    "Node Name",
                    "Is Endpoint",
                    "Is Direct Target",
                    "Activity Score",
                    "P-Value",
                    "Adjusted P-Value",
                    "Log-Probabilities (Activation, Inhibition, Others)",
                    "Pathway Activity Score",
                    "Pathway p-value",
                    "Pathway Adjusted p-value",
                    "Pathway Log-Probabilities (Activation, Inhibition, Others)",
                    "Direct Targets",
                    "Average Node Perturbation",
                    "Average Pathway Perturbation"
            }, "\t"));
            for (var p : r) {
                if (!p.isHidden()) {
                    var id = p.getId();
                    var nm = p.getName();
                    var g = p.getGraph();
                    for (var n : g.getNodes().keySet()) {
                        writeLine(ps, id, nm, n, g, data, null);
                    }
                }
            }
            for (var p : r.getVirtualPathways()) {
                List<String> nds = r.getNodesOfVirtualPathway(p).stream().map(NodeInterface::getId).collect(Collectors.toList());
                var nm = r.getNameOfVirtualPathway(p);
                var source = r.getSourceOfVirtualPathway(p);
                var g = source.getGraph();
                for (var n : nds) {
                    writeLine(ps, p, nm, n, g, data, nds);
                }
            }
        }
        return this;
    }
}
