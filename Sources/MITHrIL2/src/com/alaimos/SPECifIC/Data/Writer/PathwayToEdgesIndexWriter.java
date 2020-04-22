package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Utils.Pair;
import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class PathwayToEdgesIndexWriter extends AbstractDataWriter<Map<String, List<Pair<String, String>>>> {

    private RepositoryInterface r;

    public PathwayToEdgesIndexWriter(RepositoryInterface r) {
        this.r = r;
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<Map<String, List<Pair<String, String>>>> write(
            Map<String, List<Pair<String, String>>> data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("#Pathway Id\tPathway Name\tEdge Start\tEdge End");
            data.forEach((pId, edges) -> {
                String pName = r.getPathwayById(pId).getName();
                edges.forEach(e -> {
                    writeDelimited(ps, "\t", pId, pName, e.getFirst(), e.getSecond());
                    ps.println();
                });
            });
        }
        return this;
    }
}
