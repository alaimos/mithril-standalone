package com.alaimos.SPECifIC.CommandLine.Options;

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
public class ExportGraphOptions extends AbstractOptions {

    @Option(name = "-organism", usage = "the organism used for analysis.\nA list of organisms can be obtained by " +
            "launching the specific utility made available by this software.")
    protected String organism = "hsa";

    @Option(name = "-no", aliases = "-nodes-output", usage = "output file for nodes index.")
    protected File nodesOutput = null;

    @Option(name = "-eo", aliases = "-edges-output", usage = "output file for edges index.")
    protected File edgesOutput = null;

    @Option(name = "-mo", aliases = "-edges-map-output", usage = "output file for pathway to edges map.")
    protected File edgesMapOutput = null;

    @Option(name = "-verbose", usage = "shows verbose computational outline.")
    protected boolean verbose = false;

    @Option(name = "-disable-priority", usage = "disable priority check when building merged pathway")
    protected boolean disablePriority = false;

    @Option(name = "-enrichment-evidence-type", usage = "type of minimal evidence used when enriching pathways.")
    protected EvidenceType enrichmentEvidenceType = EvidenceType.STRONG;

    @Option(name = "-include-categories", usage = "a list of pathway categories (separated by comma) to use when " +
            "building merged pathway environment.\nOnly pathways contained in one of these categories will be " +
            "included in the computation.", metaVar = "cat1, cat2, ...")
    protected void setIncludeCategories(String s) {
        includeCategories = Arrays.stream(s.split(","))
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    private String[] includeCategories = null;

    @Option(name = "-exclude-categories", usage = "a list of pathway categories (separated by comma) to exclude when " +
            "building merged pathway environment.\nIf a pathways is contained in one of these categories then it will" +
            "  be excluded from the computation.", metaVar = "cat1, cat2, ...")
    protected void setExcludeCategories(String s) {
        excludeCategories = Arrays.stream(s.split(","))
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    private String[] excludeCategories = new String[]{
            "Endocrine and metabolic disease", "Neurodegenerative disease", "Human Diseases", "Immune disease",
            "Infectious disease: viral", "Infectious disease: parasitic", "Cardiovascular disease"
    };

    public boolean isVerbose() {
        return verbose;
    }

    public EvidenceType getEnrichmentEvidenceType() {
        return enrichmentEvidenceType;
    }

    public String getOrganism() {
        return organism;
    }

    public String[] getIncludeCategories() {
        return includeCategories;
    }

    public String[] getExcludeCategories() {
        return excludeCategories;
    }

    public File getNodesOutput() {
        return nodesOutput;
    }

    public File getEdgesOutput() {
        return edgesOutput;
    }

    public File getEdgesMapOutput() {
        return edgesMapOutput;
    }

    public boolean isDisablePriority() {
        return disablePriority;
    }
}