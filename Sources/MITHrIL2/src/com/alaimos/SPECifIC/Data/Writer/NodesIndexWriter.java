package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;

import java.io.PrintStream;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class NodesIndexWriter extends AbstractDataWriter<PathwayInterface> {

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<PathwayInterface> write(PathwayInterface data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("#Id\tName\tType\tAliases");
            for (NodeInterface n : data.getGraph().getNodes().values()) {
                writeDelimited(ps, "\t", n.getId(), n.getName(), n.getType(), concatCollection(n.getAliases(), ","));
                ps.println();
            }
        }
        return this;
    }
}
