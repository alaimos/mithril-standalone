package com.alaimos.PHENSIM.PathwayEnricher;

import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Reader.RemoteMiRNATFReader;
import com.alaimos.MITHrIL.Data.Reader.RemoteMiRNATargetsReader;
import com.alaimos.MITHrIL.Data.Records.MiRNAsContainer;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;

/**
 * Enriches pathway with miRNAs
 * <p>
 * Species is <b>required</b>.
 * <p>
 * Optional parameter <code>evidenceFilter</code> of type <code>EvidenceType</code>. Default Value <code>EvidenceType
 * .STRONG</code>.
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class MiRNAPathwayEnricher extends AbstractPathwayEnricher {

    /**
     * Get short name that identify  this enricher
     *
     * @return a short unique name
     */
    @Override
    public String getShortName() {
        return "mirna";
    }

    /**
     * Get a description for this enricher
     *
     * @return a description
     */
    @Override
    public String getDescription() {
        return "Enriches pathways with miRNAs, their targets, and their transcription factors.";
    }

    /**
     * Builds and returns the data structures which contains the data that will be used to enrich repositories.
     *
     * @return the data to use to enrich a repository
     */
    @Override
    public RepositoryEnrichmentInterface getRepositoryEnrichment() {
        if (species == null) throw new RuntimeException("You must specify a species in order to run enrichment.");
        if (!species.hasMiRNA()) return null;
        EvidenceType filter = getOptionalParameter("evidenceFilter", EvidenceType.class).orElse(EvidenceType.STRONG);
        RemoteMiRNATargetsReader rm = new RemoteMiRNATargetsReader(species.getMiRNADatabaseUrl());
        MiRNAsContainer m = rm.readMiRNAs();
        if (species.hasTF()) {
            RemoteMiRNATFReader tf = new RemoteMiRNATFReader(species.getTFDatabaseUrl());
            tf.readTranscriptionFactors(m);
        }
        return m.toEnrichment(filter);
    }
}
