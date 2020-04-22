package com.alaimos.SPECifIC.CommandLine.Options;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class ExportSubstructuresOptions extends AbstractOptions {

    @Option(name = "-organism", usage = "the organism used for analysis.\nA list of organisms can be obtained by " +
            "launching the specific utility made available by this software.")
    protected String organism = "hsa";

    @Option(name = "-i", aliases = "-in", usage = "main mithril output file which contains pathway statistics such " +
            "as Impact Factor, Accumulator, p-Value and Adjusted p-Value.", required = true)
    protected File mainInput = null;

    @Option(name = "-d", aliases = "-data", usage = "input expression file", required = true)
    protected File dataFile = null;

    protected List<String> nodesOfInterest = null;

    @Option(name = "-s", aliases = "-nodes-of-interest", usage = "a list of node identifier to use for the " +
            "computation", metaVar = "id1, id2, ...", required = false)
    protected void setNodesOfInterest(String s) {
        nodesOfInterest = Arrays.stream(s.split(","))
                                .filter(v -> !v.isEmpty())
                                .map(String::trim).collect(Collectors.toList());
    }

    @Option(name = "-p", aliases = "-perturbations", usage = "mithril perturbations file which contains all " +
            "perturbations and p-values computed for each pathway node.", forbids = "-b")
    protected File perturbationsInput = null;

    @Option(name = "-o", aliases = "-output", usage = "output file for all substructures.")
    protected File output = null;

    @Option(name = "-b", aliases = "-binary-input", usage = "reads input in binary format.", forbids = "-p")
    protected boolean binaryInput = false;

    @Option(name = "-backward", usage = "visit the graph in backward direction.")
    protected boolean backwardVisit = false;

    @Option(name = "-verbose", usage = "shows verbose computational outline.")
    protected boolean verbose = false;

    @Option(name = "-no-paths", usage = "do not write paths to the output file.")
    protected boolean noPaths = false;

    @Option(name = "-no-neighborhoods", usage = "do not write neighborhoods to the output file.")
    protected boolean noNeighborhoods = false;

    @Option(name = "-no-trees", usage = "do not write trees to the output file.")
    protected boolean noTrees = false;

    @Option(name = "-no-induced", usage = "do not write induced subgraphs to the output file.")
    protected boolean noInduced = false;

    @Option(name = "-no-communities", usage = "do not write communities to the output file.")
    protected boolean noCommunities = false;

    @Option(name = "-include-categories", usage = "a list of pathway categories (separated by comma) to use when " +
            "building merged pathway environment.\nOnly pathways contained in one of these categories will be " +
            "included in the computation.", metaVar = "cat1, cat2, ...")
    protected void setIncludeCategories(String s) {
        includeCategories = Arrays.stream(s.split(","))
                                  .filter(v -> !v.isEmpty())
                                  .map(String::trim)
                                  .toArray(String[]::new);
    }

    protected String[] includeCategories = null;

    @Option(name = "-exclude-categories", usage = "a list of pathway categories (separated by comma) to exclude when " +
            "building merged pathway environment.\nIf a pathways is contained in one of these categories then it will" +
            "  be excluded from the computation.", metaVar = "cat1, cat2, ...")
    protected void setExcludeCategories(String s) {
        excludeCategories = Arrays.stream(s.split(","))
                                  .filter(v -> !v.isEmpty())
                                  .map(String::trim)
                                  .toArray(String[]::new);
    }

    protected String[] excludeCategories = new String[]{
            "Endocrine and metabolic diseases", "Neurodegenerative diseases", "Human Diseases", "Immune diseases", "Infectious diseases",
            "Cardiovascular diseases"
    };

    @Option(name = "-min-number-of-nodes", usage = "the minimum number of nodes of a path.")
    protected int minNumberOfNodes = 5;

    @Option(name = "-max-pvalue-pathways", usage = "the maximum p-value used to determine significant pathways")
    protected double maxPValuePathways = 0.01;

    @Option(name = "-max-pvalue-nois", usage = "the maximum p-value used to detect nodes of interest")
    protected double maxPValueNoIs = 0.05;

    @Option(name = "-max-pvalue-nodes", usage = "the maximum p-value used to determine significant nodes")
    protected double maxPValueNodes = 0.05;

    @Option(name = "-max-pvalue-paths", usage = "the maximum p-value used to determine significant paths")
    protected double maxPValuePath = 1e-5;

    @Option(name = "-adjuster", usage = "the name of a p-value adjustment method: Bonferroni, Holm, " +
            "Hochberg, BH (Benjamini & Hochberg), BY (Benjamini & Yekutieli), None.")
    protected String PValueAdjuster = "holm";

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isBinaryInput() {
        return binaryInput;
    }

    public String getOrganism() {
        return organism;
    }

    public File getMainInput() {
        return mainInput;
    }

    public File getPerturbationsInput() {
        return perturbationsInput;
    }

    public File getOutput() {
        return output;
    }

    public String[] getIncludeCategories() {
        return includeCategories;
    }

    public String[] getExcludeCategories() {
        return excludeCategories;
    }

    public int getMinNumberOfNodes() {
        return minNumberOfNodes;
    }

    public double getMaxPValueNoIs() {
        return maxPValueNoIs;
    }

    public double getMaxPValueNodes() {
        return maxPValueNodes;
    }

    public double getMaxPValuePath() {
        return maxPValuePath;
    }

    public double getMaxPValuePathways() {
        return maxPValuePathways;
    }

    public List<String> getNodesOfInterest() {
        return nodesOfInterest;
    }

    public boolean isBackwardVisit() {
        return backwardVisit;
    }

    public String getPValueAdjuster() {
        return PValueAdjuster;
    }

    public File getDataFile() {
        return dataFile;
    }

    public boolean isNoNeighborhoods() {
        return noNeighborhoods;
    }

    public boolean isNoPaths() {
        return noPaths;
    }

    public boolean isNoTrees() {
        return noTrees;
    }

    public boolean isNoInduced() {
        return noInduced;
    }

    public boolean isNoCommunities() {
        return noCommunities;
    }
}