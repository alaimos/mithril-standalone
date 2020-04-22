package com.alaimos.MITHrIL.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Results.SignificantPath;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class CytoGraphPathWriter extends AbstractDataWriter<Map<String, List<SignificantPath>>> {

    protected RepositoryInterface r;

    public CytoGraphPathWriter(RepositoryInterface r) {
        this.r = r;
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<Map<String, List<SignificantPath>>> write(Map<String, List<SignificantPath>> data) {
        HashMap<String, Boolean> edges = new HashMap<>();
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("Start Node\tEdge Type\tEnd Node");
            data.forEach((pId, paths) -> {
                paths.forEach(path -> {
                    path.getEdges().forEach(e -> {
                        e.getDescriptions().forEach(ed -> {
                            String k = e.getStart().getId() + "-" + ed.getType() + "_" + ed.getSubType() + "-" +
                                    e.getEnd().getId();
                            if (!edges.containsKey(k)) {
                                edges.put(k, true);
                                ps.println(e.getStart().getId() + "\t" + ed.getType() + "_" + ed.getSubType() + "\t" +
                                        e.getEnd().getId());
                            }
                        });
                    });
                });
            });
        }
        return this;
    }
}
