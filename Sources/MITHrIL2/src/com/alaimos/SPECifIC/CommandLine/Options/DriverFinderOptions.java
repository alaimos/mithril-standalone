package com.alaimos.SPECifIC.CommandLine.Options;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import org.kohsuke.args4j.Option;

import java.io.File;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class DriverFinderOptions extends AbstractOptions {

    @Option(name = "-organism", usage = "the organism used for analysis.\nA list of organisms can be obtained by " +
            "launching the specific utility made available by this software.")
    protected String organism = "hsa";

    @Option(name = "-i", aliases = "-in", usage = "main mithril output file which contains pathway statistics such " +
            "as Impact Factor, Accumulator, p-Value and Adjusted p-Value.", required = true)
    protected File mainInput = null;

    @Option(name = "-p", aliases = "-perturbations", usage = "mithril perturbations file which contains all " +
            "perturbations and p-values computed for each pathway node.", required = true)
    protected File perturbationsInput = null;

    @Option(name = "-t", aliases = "-type", usage = "type of nodes to search for driver paths.", required = true)
    protected String nodeType = null;

    @Option(name = "-o", aliases = "-out", usage = "output file which contains computed paths and their p-values.",
            required = true)
    protected File output = null;

    @Option(name = "-combiner", usage = "the name of a p-value combination method: Fisher, Stouffer, " +
            "Mean, Logit, Wilkinson, SumOfP, VoteCounting.")
    protected String pValueCombiner = "fisher";

    @Option(name = "-min-number-of-nodes", usage = "the minimum number of nodes of a path.")
    protected int minNumberOfNodes = 5;

    @Option(name = "-enrichment-evidence-type", usage = "type of minimal evidence used when enriching pathways.")
    protected EvidenceType enrichmentEvidenceType = EvidenceType.STRONG;

    @Option(name = "-visit-nodes-once", usage = "visit each node in a pathway once.")
    protected boolean visitNodesOnce = false;

    @Option(name = "-verbose", usage = "shows verbose computational outline.")
    protected boolean verbose = false;

    @Option(name = "-max-pvalue", usage = "the maximum p-value used when computing significant paths.")
    protected double maxPValue = 0.05;

    @Option(name = "-max-pvalue-driver", usage = "the maximum p-value for each driver node.")
    protected double maxPValueDriver = 0.05;

    public boolean isVerbose() {
        return verbose;
    }

    public EvidenceType getEnrichmentEvidenceType() {
        return enrichmentEvidenceType;
    }

    public String getOrganism() {
        return organism;
    }

    public String getPValueCombiner() {
        return pValueCombiner;
    }

    public String getNodeType() {
        return nodeType;
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

    public double getMaxPValue() {
        return maxPValue;
    }

    public double getMaxPValueDriver() {
        return maxPValueDriver;
    }

    public int getMinNumberOfNodes() {
        return minNumberOfNodes;
    }

    public boolean isVisitNodesOnce() {
        return visitNodesOnce;
    }
}