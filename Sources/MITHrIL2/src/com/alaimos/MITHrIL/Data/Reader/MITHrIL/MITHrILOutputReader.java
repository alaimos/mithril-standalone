package com.alaimos.MITHrIL.Data.Reader.MITHrIL;

import com.alaimos.Commons.Reader.AbstractDataReader;
import com.alaimos.Commons.Reader.DataReaderInterface;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;

import java.io.File;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 12/12/2015
 */
public class MITHrILOutputReader extends AbstractDataReader<PathwayAnalysisResult> {

    protected File mainOutput;
    protected File nodesOutput;

    public MITHrILOutputReader(File mainOutput, File nodesOutput) {
        this.mainOutput = mainOutput;
        this.nodesOutput = nodesOutput;
        isGzipped = false;
    }

    @Override
    public DataReaderInterface<PathwayAnalysisResult> setFile(String f) {
        throw new UnsupportedOperationException("Not supported by this reader");
    }

    @Override
    public String getFile() {
        throw new UnsupportedOperationException("Not supported by this reader");
    }

    @Override
    protected PathwayAnalysisResult realReader() {
        Map<String, Double>[] main = new MainOutputReader().setFile(mainOutput).read();
        Map<String, Map<String, Double>>[] nodes = new NodesOutputReader().setFile(nodesOutput).read();
        return new PathwayAnalysisResult(nodes[0], nodes[1], main[0], main[1], main[2], main[6],
                main[3], nodes[2], main[4], main[5]);
    }

    @Override
    public PathwayAnalysisResult read() {
        return realReader();
    }
}
