package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.SPECifIC.Data.Structures.DFSResult;
import com.alaimos.SPECifIC.Data.Structures.VisitTree;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class DriversWriter extends AbstractDataWriter<Map<String, List<DFSResult>>> {

    protected RepositoryInterface r;

    public DriversWriter(RepositoryInterface r) {
        this.r = r;
    }

    @NotNull
    private static String makeKeggUrl(String pId, VisitTree path) {
        pId = pId.replace("path:", "");
        StringBuilder sb = new StringBuilder().append("http://www.kegg.jp/kegg-bin/show_pathway?").append(pId);
        NodeInterface[] nodes = path.getPathNodes();
        double[] perts = path.getPathNodesPerturbations();
        assert nodes.length == perts.length;
        NodeInterface n;
        double p;
        for (int i = 0; i < nodes.length; i++) {
            n = nodes[i];
            p = perts[i];
            sb.append("/")
              .append(n.getType().equals(NodeType.fromString("gene")) ? "hsa:" : "")
              .append(n.getId()).append("%09%23").append((p < 0) ? "0000FF" : ((p > 0) ? "FF0000" : "FFFF00"));
        }
        return sb.toString();
    }

    @NotNull
    private String printPath(EdgeInterface[] edges) {
        StringBuilder sb = new StringBuilder().append(edges[0].getStart().getId());
        for (EdgeInterface e : edges) {
            sb.append(concatArray(e.getDescriptionsStream()
                                   .map(d -> d.getSubType().symbol()).toArray(String[]::new), ""))
              .append(e.getEnd().getId());
        }
        return sb.toString();
    }

    private String[] doubleArrayToStringArray(double[] values) {
        return Arrays.stream(values).mapToObj(Double::toString).toArray(String[]::new);
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<Map<String, List<DFSResult>>> write(Map<String, List<DFSResult>> data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("# Pathway Id\tPathway Name\tStart Id\tStart Name\tEnd Id\tEnd Name\tIs Endpoint?\t" +
                    "Driver z-score\tDriver p-value\tPath p-value\tPerturbations\tPath\tKegg Link");
            data.forEach((pId, paths) -> {
                PathwayInterface p = r.getPathwayById(pId);
                List<String> endpoints = p.getGraph().getEndpoints();
                paths.sort(Comparator.comparingDouble(DFSResult::getPValue));
                paths.forEach(path -> {
                    NodeInterface start = path.getStartingNode();
                    List<VisitTree> leaves = path.getVisit().getMarkedLeaves();
                    leaves.sort(Comparator.comparingDouble(VisitTree::getPathPValue));
                    leaves.forEach(leave -> {
                        EdgeInterface[] edges = leave.getPathEdges();
                        if (edges.length == 0) return;
                        double[] perts = leave.getPathNodesPerturbations();
                        //double[] nPvs = leave.getPathNodesPValues();
                        NodeInterface end = leave.getObject().getNode();
                        writeArray(ps, new String[]{
                                pId,
                                p.getName(),
                                start.getId(),
                                start.getName(),
                                end.getId(),
                                end.getName(),
                                endpoints.contains(end.getId()) ? "Yes" : "No",
                                Double.toString(path.getZScore()),
                                Double.toString(path.getPValue()),
                                Double.toString(leave.getPathPValue()),
                                printPath(edges),
                                concatArray(doubleArrayToStringArray(perts), ";"),
                                //concatArray(doubleArrayToStringArray(nPvs), ";"),
                                makeKeggUrl(pId, leave)
                        });
                        ps.println();
                    });
                });
            });

        }
        return this;
    }
}
