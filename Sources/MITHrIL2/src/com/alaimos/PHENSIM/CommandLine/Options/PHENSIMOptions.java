package com.alaimos.PHENSIM.CommandLine.Options;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.MapOptionHandler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Arguments for the PHENSIM Command Line Service
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PHENSIMOptions extends AbstractOptions {

    @Option(name = "-organism", usage = "the organism used for analysis.\nA list of organisms can be obtained by " +
            "launching the specific utility made available by this software.")
    public String organism = "hsa";
    @Option(name = "-number-of-iterations", usage = "number of iterations for the bootstrapping procedure.")
    public int iterations = 1000;
    @Option(name = "-number-of-iterations-first-simulation", usage = "number of iterations for the simulation procedure of the input data.")
    public int simulationsFirst = 1000;
    @Option(name = "-number-of-iterations-simulation", usage = "number of iterations for the simulation procedure of the random data.")
    public int simulations = 100;
    @Option(name = "-epsilon", usage = "limit of the interval around zero that is used to identify perturbation " +
            "alterations of little importance.\nValues between -epsilon and epsilon will be considered equal to zero.")
    public double epsilon = 0.001;
    @Option(name = "-enrichment-evidence-type", usage = "type of minimal evidence used when enriching pathways.",
            forbids = "-no-enrichment")
    public EvidenceType enrichmentEvidenceType = EvidenceType.STRONG;
    @Option(name = "-weight-computation-method", usage = "the name of the weight computation method used for this run.")
    public String weightComputationMethod = "default";
    @Option(name = "-seed", usage = "for experimental reproducibility sets the seed of the random number generator.")
    public Long randomSeed = null;
    @Option(name = "-verbose", usage = "shows verbose computational outline.")
    public boolean verbose = false;
    @Option(name = "-significance-threshold", usage = "threshold used to test for activity score significance. When " +
            "absolute value of activity score is less than this threshold, the node won't be reported.")
    public Double significanceThreshold = null;
    public List<String> nonExpressedNodes = null;
    public List<String> enrichers = null;
    @Option(name = "-p", aliases = "-enrichers-params", usage = "parameters to pass to enrichers.",
            handler = MapOptionHandler.class)
    public Map<String, Object> enrichersParameters = null;
    @Option(name = "-i", aliases = "-in", usage = "a tab-separated input file where each line contains " +
            "two values: the identifier of a node and its alteration (OVEREXPRESSION, UNDEREXPRESSION, BOTH).",
            required = true)
    public File input = null;
    @Option(name = "-o", aliases = "-out", usage = "output file.", required = true)
    public File output = null;

    @Option(name = "-output-pathway-matrix", usage = "output file where the matrix of all pathway accumulators will be written")
    public File outputPathwayMatrix = null;
    @Option(name = "-output-nodes-matrix", usage = "output file where the matrix of all nodes accumulators will be written")
    public File outputNodesMatrix = null;

    @Option(name = "-t", aliases = "-threads", usage = "number of threads.")
    public int threads = 1;
    @Option(name = "-include-pathways", usage = "a file containing the list of pathways used when building the meta-pathway")
    public File includePathways = null;
    @Option(name = "-exclude-pathways", usage = "a file containing a list of pathways excluded when building the meta-pathway")
    public File excludePathways = null;
    public String[] includeCategories = null;
    public String[] excludeCategories = new String[]{
            "Endocrine and metabolic disease", "Neurodegenerative disease", "Human Diseases", "Immune disease",
            "Infectious disease: viral", "Infectious disease: parasitic", "Cardiovascular disease"
    };

    @Option(name = "-non-expressed-file", usage = "a file containing a list of non-expressed node ids.", forbids = "-non-expressed-nodes")
    public File nonExpressedFile = null;

    @Option(name = "-remove-nodes-file", usage = "a file containing a list of node ids to remove from pathways")
    public File removeNodesFile = null;

    @Option(name = "-include-categories", usage = "a list of pathway categories (separated by comma) to use when " +
            "building the meta-pathway environment.\nOnly pathways contained in one of these categories will be " +
            "included in the computation.", metaVar = "cat1, cat2, ...")
    public void setIncludeCategories(String s) {
        includeCategories = Arrays.stream(s.split(","))
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    @Option(name = "-exclude-categories", usage = "a list of pathway categories (separated by comma) to exclude when " +
            "building the meta-pathway environment.\nIf a pathways is contained in one of these categories then it will" +
            "  be excluded from the computation.", metaVar = "cat1, cat2, ...")
    public void setExcludeCategories(String s) {
        excludeCategories = Arrays.stream(s.split(","))
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    @Option(name = "-non-expressed-nodes", usage = "a list of nodes for which no expression is observed, therefore " +
            "disabling their impact on pathways.", metaVar = "id1,id2,...", forbids = "-non-expressed-file")
    public void setNonExpressedNodes(String a) {
        nonExpressedNodes = Arrays.stream(a.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }


    @Option(name = "-e", aliases = "-enrichers", usage = "enrichers used to add new features to each pathway.\n A " +
            "list can be obtained by launching the specific utility made available by this software.", metaVar =
            "name1,name2,...")
    public void setEnrichers(String a) {
        enrichers = Arrays.stream(a.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

}