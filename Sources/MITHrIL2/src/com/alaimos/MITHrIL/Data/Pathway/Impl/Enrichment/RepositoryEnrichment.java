package com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/12/2015
 */
public class RepositoryEnrichment implements RepositoryEnrichmentInterface {

    private static final long                               serialVersionUID = -1315432621342753115L;
    protected            ArrayList<EdgeEnrichmentInterface> additionalEdges  = new ArrayList<>();

    public RepositoryEnrichment() {
    }

    @Override
    public RepositoryEnrichmentInterface addAdditionalEdges(EdgeEnrichmentInterface additionalEdges) {
        this.additionalEdges.add(additionalEdges);
        return this;
    }

    @Override
    public RepositoryEnrichmentInterface addAdditionalEdges(List<EdgeEnrichmentInterface> additionalEdges) {
        this.additionalEdges.addAll(additionalEdges);
        return this;
    }

    @Override
    public RepositoryEnrichmentInterface setAdditionalEdges(List<EdgeEnrichmentInterface> additionalEdges) {
        this.additionalEdges.clear();
        return addAdditionalEdges(additionalEdges);
    }

    @Override
    public List<EdgeEnrichmentInterface> getAdditionalEdges() {
        return this.additionalEdges;
    }

    @Override
    public String toString() {
        return "RepositoryEnrichment{" +
                "additionalEdges=" + additionalEdges +
                '}';
    }
}
