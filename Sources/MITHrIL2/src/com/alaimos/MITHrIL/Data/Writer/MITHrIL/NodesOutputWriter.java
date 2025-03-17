package com.alaimos.MITHrIL.Data.Writer.MITHrIL;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;

import java.io.PrintStream;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class NodesOutputWriter extends AbstractDataWriter<PathwayAnalysisResult> {

    protected RepositoryInterface r;
    protected boolean             onlyEndpoints;

    public NodesOutputWriter(RepositoryInterface r) {
        this(r, false);
    }

    public NodesOutputWriter(RepositoryInterface r, boolean onlyEndpoints) {
        this.r = r;
        this.onlyEndpoints = onlyEndpoints;
    }

    private double multiMapGet(Map<String, Map<String, Double>> map, String key1, String key2, double defaultValue) {
        if (map.containsKey(key1)) {
            var m = map.get(key1);
            if (m != null) {
                return m.getOrDefault(key2, defaultValue);
            }
        }
        return Double.NaN;
    }

    private void writeEntry(PrintStream ps, String pId, String nId, PathwayInterface p, GraphInterface g, PathwayAnalysisResult data) {
        var pId2 = p.getId();
        writeArray(ps, new String[]{
                pId,
                pathwayName(pId),
                nId,
                g.getNode(nId).getName(),
                Double.toString(multiMapGet(data.getPerturbations(), pId2, nId, 0.0)),
                Double.toString(multiMapGet(data.getNodeAccumulators(), pId2, nId, 0.0)),
                Double.toString(multiMapGet(data.getNodePValues(), pId2, nId, 1.0))
        });
        ps.println();
    }

    /**
     * Write pathway nodes data into a print stream
     *
     * @param ps   a print stream where data will be written
     * @param p    a pathway
     * @param data other data
     */
    protected void writePathwayNodes(PrintStream ps, PathwayInterface p, PathwayAnalysisResult data) {
        if (p.isHidden()) return;
        if (!p.hasGraph()) return;
        String pId = p.getId();
        var g = p.getGraph();
        var endpoints = g.getEndpoints();
        for (var nId : g.getNodes().keySet()) {
            if (onlyEndpoints && !endpoints.contains(nId)) continue;
            writeEntry(ps, pId, nId, p, g, data);
        }
    }

    /**
     * Write virtual pathway nodes data into a print stream
     *
     * @param ps   a print stream where data will be written
     * @param pId  Virtual Pathway Id
     * @param data Pathway Analysis Results
     */
    protected void writeVirtualPathwayNodes(PrintStream ps, String pId, PathwayAnalysisResult data) {
        var n = r.getNodesOfVirtualPathway(pId);
        var s = r.getSourceOfVirtualPathway(pId);
        var g = s.getGraph();
        var endpoints = g.getEndpoints();
        for (var e1 : n) {
            var nId = e1.getId();
            if (onlyEndpoints && !endpoints.contains(nId)) continue;
            writeEntry(ps, pId, nId, s, g, data);
        }
    }

    private String pathwayName(String pId) {
        var p = r.getPathwayById(pId);
        if (p != null) return p.getName();
        if (r.hasVirtualPathway(pId)) {
            return r.getNameOfVirtualPathway(pId);
        }
        return pId;
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<PathwayAnalysisResult> write(PathwayAnalysisResult data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            ps.println("# Pathway Id\tPathway Name\tGene Id\tGene Name\tPerturbation\tAccumulator\tpValue");
            for (var p : r) {
                writePathwayNodes(ps, p, data);
            }
            for (var p : r.getVirtualPathways()) {
                writeVirtualPathwayNodes(ps, p, data);
            }
        }
        return this;
    }

}
