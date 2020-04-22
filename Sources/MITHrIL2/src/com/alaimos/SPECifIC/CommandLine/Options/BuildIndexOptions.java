package com.alaimos.SPECifIC.CommandLine.Options;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import org.kohsuke.args4j.Option;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class BuildIndexOptions extends AbstractOptions {

    @Option(name = "-organism", usage = "the organism used for analysis.\nA list of organisms can be obtained by " +
            "launching the specific utility made available by this software.")
    protected String organism = "hsa";

    @Option(name = "-enrichment-evidence-type", usage = "type of minimal evidence used when enriching pathways.")
    protected EvidenceType enrichmentEvidenceType = EvidenceType.STRONG;

    @Option(name = "-verbose", usage = "shows verbose computational outline.")
    protected boolean verbose = false;


    public boolean isVerbose() {
        return verbose;
    }

    public EvidenceType getEnrichmentEvidenceType() {
        return enrichmentEvidenceType;
    }

    public String getOrganism() {
        return organism;
    }

}