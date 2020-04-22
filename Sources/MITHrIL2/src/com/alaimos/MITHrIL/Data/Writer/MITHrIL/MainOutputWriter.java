package com.alaimos.MITHrIL.Data.Writer.MITHrIL;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class MainOutputWriter extends AbstractDataWriter<PathwayAnalysisResult> {

    protected RepositoryInterface r;

    public MainOutputWriter(RepositoryInterface r) {
        this.r = r;
    }

    private String pathwayName(String pId) {
        var p = r.getPathwayById(pId);
        if (p != null) return p.getName();
        if (r.hasVirtualPathway(pId)) {
            return r.getNameOfVirtualPathway(pId);
        }
        return pId;
    }

    private boolean pathwayIsHidden(String pId) {
        var p = r.getPathwayById(pId);
        return (p != null) && p.isHidden();
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<PathwayAnalysisResult> write(PathwayAnalysisResult data) {
        try (var ps = new PrintStream(getOutputStream())) {
            ps.println("# Pathway Id\tPathway Name\tRaw Accumulator\tImpact Factor\tProbability Pi\t" +
                    "Probability Network\tCorrected Accumulator\tpValue\tAdjusted pValue");
            var acc = data.getAccumulators();
            var imf = data.getImpactFactors();
            var pro = data.getProbabilities();
            var net = data.getProbabilitiesNetwork();
            var corrAcc = data.getCorrectedAccumulators();
            var pvs = data.getPValues();
            var l = new ArrayList<>(data.getAdjustedPValues().entrySet());
            Comparator<Map.Entry<String, Double>> compAcc = Comparator.comparingDouble(o -> Math.abs(corrAcc.get(o.getKey())));
            Comparator<Map.Entry<String, Double>> c = Comparator.comparingDouble(Map.Entry::getValue);
            c = c.thenComparing(compAcc.reversed());
            l.sort(c);
            for (var e : l) {
                var pId = e.getKey();
                if (!pathwayIsHidden(pId)) {
                    writeArray(ps, new String[]{
                            pId,
                            pathwayName(pId),
                            Double.toString(acc.get(pId)),
                            Double.toString(imf.get(pId)),
                            Double.toString(pro.get(pId)),
                            Double.toString(net.get(pId)),
                            Double.toString(corrAcc.get(pId)),
                            Double.toString(pvs.get(pId)),
                            Double.toString(e.getValue())
                    });
                    ps.println();
                }
            }
        }
        return this;
    }
}
