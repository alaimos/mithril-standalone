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
public class MITHrILCommonOptions extends AbstractOptions {

    @Option(name = "-organism", usage = "the organism used for analysis.")
    public String       organism                = "hsa";
    @Option(name = "-combiner", usage = "the name of a p-value combination method: Fisher, Stouffer, Mean, Logit, Wilkinson, SumOfP, VoteCounting.")
    public String       pValueCombiner          = "stouffer";
    @Option(name = "-adjuster", usage = "the name of a p-value adjustment method: Bonferroni, Holm, Hochberg, BH (Benjamini & Hochberg), BY " +
            "(Benjamini & Yekutieli), None.")
    public String       pValueAdjuster          = "BH";
    @Option(name = "-weight-computation-method", usage = "the name of the weight computation method used for this run.")
    public String       weightComputationMethod = "default";
    @Option(name = "-number-of-iterations", usage = "number of iterations for the p-value computation.")
    public int          pValueIterations        = 2001;
    @Option(name = "-decoys", usage = "adds decoy pathways.")
    public boolean      decoys                  = false;
    @Option(name = "-no-enrichment", usage = "disable pathway enrichment with miRNAs.", forbids = "-enrichment-evidence-type")
    public boolean      noEnrichment            = false;
    @Option(name = "-enrichment-evidence-type", usage = "type of minimal evidence used when enriching pathways.", forbids = "-no-enrichment")
    public EvidenceType enrichmentEvidenceType  = EvidenceType.STRONG;
    @Option(name = "-filter-output", usage = "a file containing a list of pathways to be shown in the output files")
    public File         outputFilter            = null;
    @Option(name = "-seed", usage = "for experimental reproducibility sets the seed of the random number generator.")
    public Long         randomSeed              = null;
    @Option(name = "-verbose", usage = "shows verbose computational outline.")
    public boolean      verbose                 = false;
    @Option(name = "-m", aliases = "-meta-pathway", usage = "run all algorithms on a meta-pathway obtained by merging all pathways in the internal " +
            "repository.")
    public boolean      metaPathway             = false;
    @Option(name = "-no-complete", usage = "do not complete pathways with missing elements.")
    public boolean      noCompletePathway       = false;
    @Option(name = "-include-pathways", usage = "a file containing the list of pathways used when building the meta-pathway", depends = "-m")
    public File         includePathways         = null;
    @Option(name = "-exclude-pathways", usage = "a file containing a list of pathways excluded when building the meta-pathway", depends = "-m")
    public File         excludePathways         = null;
    public String[]     includeCategories       = null;
    public String[]     excludeCategories       = new String[]{
            "Endocrine and metabolic diseases", "Neurodegenerative diseases", "Human Diseases", "Immune diseases", "Infectious diseases",
            "Cardiovascular diseases"
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