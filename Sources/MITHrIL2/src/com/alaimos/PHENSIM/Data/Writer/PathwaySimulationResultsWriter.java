package com.alaimos.PHENSIM.Data.Writer;

import com.alaimos.Commons.Math.PValue.Adjusters;
import com.alaimos.Commons.Utils.Utils;
import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Results.PathwaySimulationResults;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alaimos.PHENSIM.Data.Results.PathwaySimulationResults.Computation.ACTIVITY;
import static com.alaimos.PHENSIM.Data.Results.PathwaySimulationResults.State.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PathwaySimulationResultsWriter extends AbstractDataWriter<Map<String, PathwaySimulationResults>> {

    protected RepositoryInterface r;
    private Double significanceThreshold;
    private HashMap<String, Double> pathwayProbabilites;

    public PathwaySimulationResultsWriter(RepositoryInterface r, Double significanceThreshold) {
        this.r = r;
        this.significanceThreshold = significanceThreshold;
        this.pathwayProbabilites = new HashMap<>();
    }

    private void writeLine(PrintStream ps, String pathwayId, String pathwayName, String nodeId, GraphInterface sourceGraph,
                           @NotNull PathwaySimulationResults sourceRes, PathwaySimulationResults pathwayRes, List<String> nodesFilter,
                           double pathPV) {
        var as = sourceRes.getActivityScore(nodeId);
        if (significanceThreshold == null || Math.abs(as) > significanceThreshold) {
            var ds = sourceRes.getDirectlyTargetedBy();
            var tgts = "";
            if (ds.containsKey(nodeId)) {
                var tmp = ds.get(nodeId).stream();
                if (nodesFilter != null) {
                    tmp = tmp.filter(nodesFilter::contains);
                }
                tgts = tmp.collect(Collectors.joining(","));
            }
            writeArray(ps, new String[]{
                    pathwayId,
                    pathwayName,
                    nodeId,
                    sourceGraph.getNode(nodeId).getName(),
                    (sourceGraph.getEndpoints().contains(nodeId)) ? "Yes" : "No",
                    (sourceRes.getDirectTargets().contains(nodeId)) ? "Yes" : "No",
                    Double.toString(as),
                    Double.toString(sourceRes.getPValue(nodeId)),
                    concatArray(new String[]{
                            Double.toString(sourceRes.getLikelihoodRatio(nodeId, ACTIVE)),
                            Double.toString(sourceRes.getLikelihoodRatio(nodeId, INHIBITED)),
                            Double.toString(sourceRes.getLikelihoodRatio(nodeId, OTHERWISE))
                    }, ","),
                    Double.toString(pathwayRes.getPathwayActivityScore()),
                    Double.toString(pathPV),
                    concatArray(new String[]{
                            Double.toString(pathwayRes.getLikelihoodRatio(null, ACTIVE)),
                            Double.toString(pathwayRes.getLikelihoodRatio(null, INHIBITED)),
                            Double.toString(pathwayRes.getLikelihoodRatio(null, OTHERWISE))
                    }, ","),
                    tgts,
                    concatArray(new String[]{
                            Double.toString(sourceRes.getLogProbability(nodeId, ACTIVITY, ACTIVE)),
                            Double.toString(sourceRes.getLogProbability(nodeId, ACTIVITY, INHIBITED)),
                            Double.toString(sourceRes.getLogProbability(nodeId, ACTIVITY, OTHERWISE))
                    }, ",")
            });
            ps.println();
        }
    }

    @NotNull
    private Map<String, Double> getPathwayPValues(Map<String, PathwaySimulationResults> data) {
        Map<String, Double> result = new HashMap<>();
        for (var p : r) {
            if (!p.isHidden()) {
                var id = p.getId();
                result.put(id, data.get(id).getPValue(null));
            }
        }
        for (var p : r.getVirtualPathways()) {
            result.put(p, data.get(p).getPValue(null));
        }
        Utils.applyArrayFunctionToMap(result, v -> {
            var r = Adjusters.benjaminiHochberg(Arrays.stream(v).mapToDouble(x -> x).toArray());
            return Arrays.stream(r).boxed().toArray(Double[]::new);
        }, String.class, Double.class);
        return result;
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<Map<String, PathwaySimulationResults>> write(
            Map<String, PathwaySimulationResults> data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("# Pathway Id\tPathway Name\tNode Id\tNode Name\tIs Endpoint\tIs Direct Target\tActivity Score\tP-Values\tLog-Likelihoods " +
                    "(Activation, Inhibition, Others)\tPathway Activity Score\tPathway p-value\tPathway Log-Likelihoods\tDirect Targets\tProbabilities (Activation, " +
                    "Inhibition, Others)");
            var pvs = getPathwayPValues(data);
            for (var p : r) {
                if (!p.isHidden()) {
                    var id = p.getId();
                    var nm = p.getName();
                    var g = p.getGraph();
                    for (var n : g.getNodes().keySet()) {
                        writeLine(ps, id, nm, n, g, data.get(id), data.get(id), null, pvs.get(id));
                    }
                }
            }
            for (var p : r.getVirtualPathways()) {
                List<String> nds = r.getNodesOfVirtualPathway(p).stream().map(NodeInterface::getId).collect(Collectors.toList());
                var nm = r.getNameOfVirtualPathway(p);
                var source = r.getSourceOfVirtualPathway(p);
                var sId = source.getId();
                var g = source.getGraph();
                for (var n : nds) {
                    writeLine(ps, p, nm, n, g, data.get(sId), data.get(p), nds, pvs.get(p));
                }
            }
        }
        return this;
    }
}
