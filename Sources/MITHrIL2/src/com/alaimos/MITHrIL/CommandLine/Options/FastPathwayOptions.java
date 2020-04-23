package com.alaimos.MITHrIL.CommandLine.Options;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.Arrays;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 05/01/2016
 */
public class FastPathwayOptions extends AbstractOptions {

    @Option(name = "-i", aliases = "-in", usage = "a tab-separated input file where each line contains " +
            "a node identifier (gene, microRNA, metabolite) and its Log-Fold-Change for each experiment.\n" +
            "If the Log-Fold-Change is absent or zero, the gene is assumed as non-differentially expressed.\n" +
            "Genes are identified by EntrezId, microRNA by mature name (miRBase release 21), metabolites or " +
            "chemicals by KEGG id.\nThe first line of the file MUST contain names for each experiment. No spaces or " +
            "symbols are allowed for experiment names.", required = true)
    public File input = null;

    @Option(name = "-o", aliases = "-out", usage = "output file.", required = true)
    public File output = null;

    @Option(name = "-threads", usage = "the number of threads you wish to spawn.")
    public int threads = Runtime.getRuntime().availableProcessors() - 2;

    @Option(name = "-buffer-size", usage = "the size of the input and output buffers.")
    public int bufferSize = 10 * threads;

    @Option(name = "-organism", usage = "the organism used for analysis.")
    public String organism = "hsa";

    @Option(name = "-accumulator", usage = "compute accumulators instead of pathway perturbations.")
    public boolean accumulator = false;

    @Option(name = "-enrichment-evidence-type", usage = "type of minimal evidence used when enriching pathways.", forbids = "-no-enrichment")
    public EvidenceType enrichmentEvidenceType = EvidenceType.STRONG;

    @Option(name = "-cycle-test", usage = "use test cycle breaking method (0 is disabled).")
    public int cycleTest = 0;

    @Option(name = "-include-pathways", usage = "a file containing the list of pathways used when building the meta-pathway", depends = "-m")
    public File includePathways = null;

    @Option(name = "-exclude-pathways", usage = "a file containing a list of pathways excluded when building the meta-pathway", depends = "-m")
    public File excludePathways = null;
    public String[] includeCategories = null;
    public String[] excludeCategories = new String[]{
            "Endocrine and metabolic disease", "Neurodegenerative disease", "Human Diseases", "Immune disease",
            "Infectious disease: viral", "Infectious disease: parasitic", "Cardiovascular disease"
    };

    @Option(name = "-include-categories", usage = "a list of pathway categories (separated by comma) to use when " +
            "building the meta-pathway environment.\nOnly pathways contained in one of these categories will be " +
            "included in the computation.", metaVar = "cat1, cat2, ...", depends = "-m")
    public void setIncludeCategories(String s) {
        includeCategories = Arrays.stream(s.split(","))
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    @Option(name = "-exclude-categories", usage = "a list of pathway categories (separated by comma) to exclude when " +
            "building the meta-pathway environment.\nIf a pathways is contained in one of these categories then it will" +
            "  be excluded from the computation.", metaVar = "cat1, cat2, ...", depends = "-m")
    public void setExcludeCategories(String s) {
        excludeCategories = Arrays.stream(s.split(","))
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }


}