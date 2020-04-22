package com.alaimos.MITHrIL.CommandLine.Options;

import org.kohsuke.args4j.Option;

import java.io.File;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 05/01/2016
 */
public class MITHrILBatchOptions extends MITHrILCommonOptions {

    @Option(name = "-i", aliases = "-in", usage = "a tab-separated input file where each line contains " +
            "a node identifier (gene, microRNA, metabolite) and its Log-Fold-Change for each experiment.\n" +
            "If the Log-Fold-Change is absent or zero, the gene is assumed as non-differentially expressed.\n" +
            "Genes are identified by EntrezId, microRNA by mature name (miRBase release 21), metabolites or " +
            "chemicals by KEGG id.\nThe first line of the file MUST contain names for each experiment. No spaces or " +
            "symbols are allowed for experiment names.", required = true)
    public File    input        = null;
    @Option(name = "-o", aliases = "-out", usage = "output directory.", required = true)
    public File    outputDir    = null;
    @Option(name = "-b", aliases = "-binary-output", usage = "Should output be stored in binary format?")
    public boolean binaryOutput = false;
    @Option(name = "-threads", usage = "the number of threads you wish to spawn. If 0 the number is automatically detected.")
    public int     threads      = 0;

}