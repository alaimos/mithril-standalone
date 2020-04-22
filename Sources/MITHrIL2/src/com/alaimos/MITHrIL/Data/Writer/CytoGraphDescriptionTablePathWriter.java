package com.alaimos.MITHrIL.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Results.SignificantPath;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class CytoGraphDescriptionTablePathWriter extends AbstractDataWriter<Map<String, List<SignificantPath>>> {

    protected RepositoryInterface r;

    public CytoGraphDescriptionTablePathWriter(RepositoryInterface r) {
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
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("Node Id\tName\tType\tPerturbation\tp-Value");
            data.forEach((pId, paths) -> {
                paths.forEach(path -> {
                    NodeInterface node;
                    double acc, pv;
                    List<NodeInterface> nodes = path.getNodes();
                    for (int i = 0; i < nodes.size(); i++) {
                        node = nodes.get(i);
                        acc = path.getNodesPerturbation().get(i);
                        pv = path.getNodesPValues().get(i);
                        writeArray(ps, new String[]{
                                node.getId(),
                                node.getName(),
                                node.getType().toString(),
                                Double.toString(acc),
                                Double.toString(pv)
                        });
                        ps.println();
                    }
                });
            });
        }
        return this;
    }
}
