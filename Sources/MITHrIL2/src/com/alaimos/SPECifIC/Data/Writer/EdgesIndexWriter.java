package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeDescriptionInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;

import java.io.PrintStream;
import java.util.Map;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class EdgesIndexWriter extends AbstractDataWriter<PathwayInterface> {

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<PathwayInterface> write(PathwayInterface data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("#Start\tEnd\tType\tSubtype");
            for (Map<String, EdgeInterface> el : data.getGraph().getEdges().values()) {
                for (EdgeInterface e : el.values()) {
                    for (EdgeDescriptionInterface ed : e.getDescriptions()) {
                        writeDelimited(ps, "\t", e.getStart().getId(), e.getEnd().getId(), ed.getType(),
                                ed.getSubType());
                        ps.println();
                    }
                }
            }
        }
        return this;
    }
}
