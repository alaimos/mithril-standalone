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
 * Arguments for the Export SubGraph Command Line Service
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class ExportSubGraphOptions extends AbstractOptions {

    @Option(name = "-organism", usage = "the organism used for analysis.\nA list of organisms can be obtained by " +
            "launching the specific utility made available by this software.")
    public String organism = "hsa";
    @Option(name = "-enrichment-evidence-type", usage = "type of minimal evidence used when enriching pathways.",
            forbids = "-no-enrichment")
    public EvidenceType enrichmentEvidenceType = EvidenceType.STRONG;
    @Option(name = "-verbose", usage = "shows verbose computational outline.")
    public boolean verbose = false;

    public List<String> enrichers = null;
    @Option(name = "-p", aliases = "-enrichers-params", usage = "parameters to pass to enrichers.",
            handler = MapOptionHandler.class)
    public Map<String, Object> enrichersParameters = null;

    @Option(name = "-include-pathways", usage = "a file containing the list of pathways used when building the meta-pathway")
    public File includePathways = null;
    @Option(name = "-exclude-pathways", usage = "a file containing a list of pathways excluded when building the meta-pathway")
    public File excludePathways = null;
    public String[] includeCategories = null;
    public String[] excludeCategories = new String[]{
            "Endocrine and metabolic disease", "Neurodegenerative disease", "Human Diseases", "Immune disease",
            "Infectious disease: viral", "Infectious disease: parasitic", "Cardiovascular disease"
    };
    public String[] inputNodes = null;

    @Option(name = "-i", aliases = "-input", usage = "a list of input nodes.", metaVar = "id1, id2, ...", required = true)
    public void setInputNodes(String s) {
        inputNodes = Arrays.stream(s.split(",", -1))
                .filter(v -> !v.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    @Option(name = "-o", aliases = "-out", usage = "output file.", required = true)
    public File output = null;


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

    @Option(name = "-e", aliases = "-enrichers", usage = "enrichers used to add new features to each pathway.\n A " +
            "list can be obtained by launching the specific utility made available by this software.", metaVar =
            "name1,name2,...")
    public void setEnrichers(String a) {
        enrichers = Arrays.stream(a.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

}