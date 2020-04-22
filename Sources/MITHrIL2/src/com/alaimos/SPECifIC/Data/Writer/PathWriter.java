package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.SPECifIC.Data.Structures.VisitTree;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class PathWriter extends AbstractDataWriter<VisitTree> {

    private boolean append = false;

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    @SuppressWarnings("unchecked")
    public DataWriterInterface<VisitTree> write(VisitTree data) {
        try (PrintStream ps = new PrintStream(getOutputStream(append))) {
            if (!append) {
                ps.println("#Start\tEnd\tAccumulator\tNodes\tp-value");
            }
            ps.println("#BEGIN PATH (" + data.getObject().getNode().getId() + ")");
            data.getMarkedLeavesStream().forEach(leaf -> {
                NodeInterface[] nodes = leaf.getPathNodes();
                String nodesString = Arrays.stream(nodes)
                                           .map(NodeInterface::getId)
                                           .collect(Collectors.joining(","));
                writeDelimited(ps, "\t", nodes[0].getId(), nodes[nodes.length - 1].getId(), leaf.getPathAccumulator(),
                        nodesString, leaf.getPathPValue());
                ps.println();
            });
            ps.println("#END PATH (" + data.getObject().getNode().getId() + ")");
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
    public DataWriterInterface<VisitTree> write(File f, VisitTree data) {
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
    public DataWriterInterface<VisitTree> write(File f, VisitTree data, boolean append) {
        this.append = append;
        return setFile(f).write(data);
    }
}
