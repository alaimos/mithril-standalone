package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Math.PValue.Adjuster;
import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.EmpiricalBrownsMethod;
import com.alaimos.Commons.Utils.Triple;
import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.SPECifIC.CommandLine.Options.ExportSubstructuresOptions;
import com.alaimos.SPECifIC.Data.Structures.CommunitySubGraph;
import com.alaimos.SPECifIC.Data.Structures.GraphVisitNode;
import com.alaimos.SPECifIC.Data.Structures.InducedSubGraph;
import com.alaimos.SPECifIC.Data.Structures.VisitTree;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class AllWriter<T extends Triple<VisitTree[], InducedSubGraph[], CommunitySubGraph>>
        extends AbstractDataWriter<T> {

    public static int writtenCount = 0;
    private ExportSubstructuresOptions options;
    private boolean append           = false;
    private int     minNumberOfNodes = 0;
    private double  maxPValue        = 0.00001;
    private Adjuster pvAdjuster;

    public AllWriter(int minNumberOfNodes, double maxPValue, Adjuster pvAdjuster, ExportSubstructuresOptions options) {
        this.minNumberOfNodes = minNumberOfNodes;
        this.maxPValue = maxPValue;
        this.pvAdjuster = pvAdjuster;
        this.options = options;
    }

    private String edgesStringCollector(Stream<EdgeInterface> s) {
        return s.map(e -> e.getStart().getId() + "," + e.getEnd().getId())
                .collect(Collectors.joining(";"));
    }

    private double[] collectPValues(VisitTree[] trees, InducedSubGraph[] inducedSubGraphs,
                                    CommunitySubGraph communitySubGraph) {
        ArrayList<Double> pValues = new ArrayList<>();
        for (VisitTree t : trees) {
            t.getMarkedLeavesStream().forEach(leave -> pValues.add(leave.getPathPValue()));
            pValues.add(t.getNeighborhoodPValue());
            pValues.add(t.getTreePValue());
        }
        if (inducedSubGraphs != null) {
            for (InducedSubGraph i : inducedSubGraphs) {
                pValues.add(i.getGraphPValue());
            }
        }
        if (communitySubGraph != null) {
            pValues.add(communitySubGraph.getGraphPValue());
        }
        return pValues.stream().mapToDouble(t -> t).toArray();
    }

    private void adjustPValues(VisitTree[] trees, InducedSubGraph[] inducedSubGraphs,
                               CommunitySubGraph communitySubGraph) {
        double[] pValues = collectPValues(trees, inducedSubGraphs, communitySubGraph);
        double[] pvAdjusted = pvAdjuster.adjust(pValues);
        assert pValues.length == pvAdjusted.length;
        int i = 0;
        for (VisitTree t : trees) {
            for (VisitTree l : t.getMarkedLeaves()) {
                l.setAdjustedPathPValue(pvAdjusted[i++]);
            }
            t.setAdjustedNeighborhoodPValue(pvAdjusted[i++]);
            t.setAdjustedTreePValue(pvAdjusted[i++]);
        }
        if (inducedSubGraphs != null) {
            for (InducedSubGraph inducedSubGraph : inducedSubGraphs) {
                inducedSubGraph.setAdjustedPValue(pvAdjusted[i++]);
            }
        }
        if (communitySubGraph != null) {
            communitySubGraph.setAdjustedPValue(pvAdjusted[i]);
        }
    }

    private void pathWriter(VisitTree data, PrintStream ps) {
        EmpiricalBrownsMethod eb = new EmpiricalBrownsMethod();
        data.getMarkedLeavesStream().forEach(leaf -> {
            NodeInterface[] nodes = leaf.getPathNodes();
            EdgeInterface[] edges = leaf.getPathEdges();
            String nodesString = Arrays.stream(nodes)
                                       .map(NodeInterface::getId)
                                       .collect(Collectors.joining(";"));
            String edgesString = edgesStringCollector(Arrays.stream(edges));
            if (nodes.length >= minNumberOfNodes && leaf.getAdjustedPathPValue() <= maxPValue) {
                writeDelimited(ps, "\t", nodes[0].getId(), "path", leaf.getPathAccumulator(), //leaf.getPathPValue(),
                               leaf.getAdjustedPathPValue(), nodesString, edgesString);
                ps.println();
                writtenCount++;
            }
        });
    }

    private void neighborhoodWriter(VisitTree data, PrintStream ps) {
        String neighbors = data.getObject().getNode().getId() + ";" + data.children().stream()
                                                                          .map(VisitTree::getObject)
                                                                          .map(GraphVisitNode::getNode)
                                                                          .map(NodeInterface::getId)
                                                                          .collect(Collectors.joining(";"));
        String edges = edgesStringCollector(
                data.children().stream().map(c -> c.getObject().getEdge()).filter(Objects::nonNull));
        if (neighbors.split(";").length >= minNumberOfNodes && data.getAdjustedNeighborhoodPValue() <= maxPValue) {
            writeDelimited(ps, "\t", data.getObject().getNode().getId(), "neighborhood",
                           data.getNeighborhoodAccumulator(),
                    /*data.getNeighborhoodPValue(),*/ data.getAdjustedNeighborhoodPValue(), neighbors, edges);
            ps.println();
            writtenCount++;
        }
    }

    private void treeWriter(VisitTree data, PrintStream ps) {
        String nodes = data.stream().map(c -> c.getObject().getNode().getId())
                           .collect(Collectors.joining(";"));
        String edges = edgesStringCollector(data.stream().map(c -> c.getObject().getEdge()).filter(Objects::nonNull));
        if (nodes.split(";").length >= minNumberOfNodes && data.getAdjustedTreePValue() <= maxPValue) {
            writeDelimited(ps, "\t", data.getObject().getNode().getId(), "tree", data.getTreeAccumulator(),
                    /*data.getTreePValue(),*/ data.getAdjustedTreePValue(), nodes, edges);
            ps.println();
            writtenCount++;
        }
    }

    private void subGraphWriter(InducedSubGraph data, String root, String type, PrintStream ps) {
        String nodes = Arrays.stream(data.getNodes()).map(NodeInterface::getId)
                             .collect(Collectors.joining(";"));
        String edges = edgesStringCollector(Arrays.stream(data.getEdges()));
        if (data.getNodes().length >= minNumberOfNodes && data.getAdjustedPValue() <= maxPValue) {
            writeDelimited(ps, "\t", root, type, data.getAccumulator(), /*data.getGraphPValue(),*/
                           data.getAdjustedPValue(),
                           nodes, edges);
            ps.println();
            writtenCount++;
        }
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<T> write(T data) {
        try (PrintStream ps = new PrintStream(getOutputStream(append))) {
            adjustPValues(data.getFirst(), data.getSecond(), data.getThird());
            if (!append) {
                ps.println("#Root\tType\tAccumulator\tp-value\tNodes\tEdges");
            }
            for (VisitTree t : data.getFirst()) {
                if (!options.isNoPaths()) pathWriter(t, ps);
                if (!options.isNoNeighborhoods()) neighborhoodWriter(t, ps);
                if (!options.isNoTrees()) treeWriter(t, ps);
            }
            if (data.getSecond() != null && !options.isNoInduced()) {
                InducedSubGraph sg;
                VisitTree t;
                for (int i = 0; i < data.getSecond().length; i++) {
                    sg = data.getSecond()[i];
                    t = data.getFirst()[i];
                    subGraphWriter(sg, t.getObject().getNode().getId(), "induced-subgraph", ps);
                }
            }
            if (data.getThird() != null && !options.isNoCommunities()) {
                String root = Arrays.stream(data.getFirst())
                                    .map(c -> c.getObject().getNode().getId())
                                    .collect(Collectors.joining(";"));
                subGraphWriter(data.getThird(), root, "community", ps);
            }
        }
        return this;
    }

    /**
     * Set filename and write data into it
     *
     * @param f    the file
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    public DataWriterInterface<T> write(File f, T data) {
        this.append = false;
        return setFile(f).write(data);
    }

    /**
     * Set filename and write data into it
     *
     * @param f      the file
     * @param data   the data that will be written into a file
     * @param append append the data?
     * @return this object for a fluent interface
     */
    public DataWriterInterface<T> write(File f, T data, boolean append) {
        this.append = append;
        return setFile(f).write(data);
    }
}
