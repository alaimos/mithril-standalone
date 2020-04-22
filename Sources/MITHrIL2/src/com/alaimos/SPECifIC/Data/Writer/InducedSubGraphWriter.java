package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.SPECifIC.Data.Structures.InducedSubGraph;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class InducedSubGraphWriter extends AbstractDataWriter<InducedSubGraph> {

    private boolean append = false;

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<InducedSubGraph> write(InducedSubGraph data) {
        try (PrintStream ps = new PrintStream(getOutputStream(append))) {
            ps.println("#BEGIN GRAPH");
            ps.println(data.getAccumulator() + "\t" + data.getGraphPValue());
            ps.println("#BEGIN NODES");
            String[] nodes = Arrays.stream(data.getNodes()).map(NodeInterface::getId).toArray(String[]::new);
            writeArray(ps, nodes, ",");
            ps.println();
            ps.println("#END NODES\n#BEGIN EDGES");
            for (EdgeInterface e : data.getEdges()) {
                writeDelimited(ps, "\t", e.getStart().getId(), e.getEnd().getId());
                ps.println();
            }
            ps.println("#END EDGES\n#END GRAPH");
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
    public DataWriterInterface<InducedSubGraph> write(File f, InducedSubGraph data) {
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
    public DataWriterInterface<InducedSubGraph> write(File f, InducedSubGraph data, boolean append) {
        this.append = append;
        return setFile(f).write(data);
    }
}
