package com.alaimos.MITHrIL.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import com.alaimos.MITHrIL.Data.Results.SignificantPath;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class SignificantPathsWriter extends AbstractDataWriter<Map<String, List<SignificantPath>>> {

    protected RepositoryInterface r;

    public SignificantPathsWriter(RepositoryInterface r) {
        this.r = r;
    }

    @NotNull
    private static String makeKeggUrl(String pId, SignificantPath path) {
        pId = pId.replace("path:", "");
        StringBuilder sb = new StringBuilder().append("http://www.kegg.jp/kegg-bin/show_pathway?").append(pId);
        List<NodeInterface> nodes = path.getNodes();
        List<Double> perts = path.getNodesPerturbation();
        assert nodes.size() == perts.size();
        NodeInterface n;
        double p;
        for (int i = 0; i < nodes.size(); i++) {
            n = nodes.get(i);
            p = perts.get(i);
            sb.append("/")
              .append(n.getType().equals(NodeType.fromString("gene")) ? "hsa:" : "")
              .append(n.getId()).append("%09%23").append((p < 0) ? "00A9FF" : ((p > 0) ? "FFA900" : "FFFF00"));
        }
        return sb.toString();
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<Map<String, List<SignificantPath>>> write(Map<String, List<SignificantPath>> data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println(
                    "# Pathway Id\tPathway Name\tEndpoint Id\tEndpoint Name\tp-value\tPath\tPerturbations\tKegg Link");
            data.forEach((pId, paths) -> {
                PathwayInterface p = r.getPathwayById(pId);
                //if (paths.size() > 0) System.out.println(p.getName());
                paths.sort(Comparator.comparingDouble(SignificantPath::getPathPValue));
                paths.forEach(path -> {
                    List<EdgeInterface> edges = path.getEdges();
                    List<Double> perts = path.getNodesPerturbation();
                    NodeInterface endpoint = edges.get(edges.size() - 1).getEnd();
                    //System.out.println("    " + path.toString());
                    writeArray(ps, new String[]{
                            pId,
                            p.getName(),
                            endpoint.getId(),
                            endpoint.getName(),
                            Double.toString(path.getPathPValue()),
                            concatArray(edges.stream().map(e -> concatArray(new String[]{
                                    e.getStart().getId(),
                                    concatArray(e.getDescriptionsStream().map(d -> concatArray(new String[]{
                                            d.getType().toString(),
                                            d.getSubType().toString()
                                    }, ":")).toArray(String[]::new), "--"),
                                    e.getEnd().getId()
                            }, ",")).toArray(String[]::new), ";"),
                            concatArray(perts.stream().map(d -> Double.toString(d)).toArray(String[]::new), ";"),
                            makeKeggUrl(pId, path)
                    });
                    ps.println();
                });
            });

        }
        return this;
    }
}
