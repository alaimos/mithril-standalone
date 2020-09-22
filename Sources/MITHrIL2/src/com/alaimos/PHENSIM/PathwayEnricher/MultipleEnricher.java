package com.alaimos.PHENSIM.PathwayEnricher;

import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Enriches pathway with multiple enrichers
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class MultipleEnricher extends AbstractPathwayEnricher {

    protected List<PathwayEnricherInterface> enrichers = new ArrayList<>();

    /**
     * Get short name that identify  this enricher
     *
     * @return a short unique name
     */
    @Override
    public String getShortName() {
        return null;
    }

    /**
     * Get a description for this enricher
     *
     * @return a description
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * Set a new list of enrichers
     *
     * @param enrichers the list of enrichers
     * @return this object for a fluent interface
     */
    public MultipleEnricher setEnrichers(List<PathwayEnricherInterface> enrichers) {
        return clearEnrichers().addEnrichers(enrichers);
    }

    /**
     * Adds a new list of enrichers to the current one
     *
     * @param enrichers the list of enrichers
     * @return this object for a fluent interface
     */
    public MultipleEnricher addEnrichers(List<PathwayEnricherInterface> enrichers) {
        this.enrichers.addAll(enrichers);
        return this;
    }

    /**
     * Set a new enricher
     *
     * @param enricher an enricher name
     * @return this object for a fluent interface
     */
    public MultipleEnricher setEnricher(String enricher) {
        return setEnricher(PathwayEnricherFactory.getInstance().getPathwayEnricher(enricher));
    }

    /**
     * Set a new enricher
     *
     * @param enricher an enricher
     * @return this object for a fluent interface
     */
    public MultipleEnricher setEnricher(PathwayEnricherInterface enricher) {
        return clearEnrichers().addEnricher(enricher);
    }

    /**
     * Add a new enricher
     *
     * @param enricher the name of an enricher
     * @return this object for a fluent interface
     */
    public MultipleEnricher addEnricher(String enricher) {
        return addEnricher(PathwayEnricherFactory.getInstance().getPathwayEnricher(enricher));
    }

    /**
     * Add a new enricher
     *
     * @param enricher an enricher
     * @return this object for a fluent interface
     */
    public MultipleEnricher addEnricher(PathwayEnricherInterface enricher) {
        enrichers.add(enricher);
        return this;
    }

    /**
     * Clears the list of enrichers
     *
     * @return this object for a fluent interface
     */
    public MultipleEnricher clearEnrichers() {
        enrichers.clear();
        return this;
    }

    /**
     * Builds and returns the data structures which contains the data that will be used to enrich repositories.
     *
     * @return the data to use to enrich a repository
     */
    @Override
    public RepositoryEnrichmentInterface getRepositoryEnrichment() {
        throw new RuntimeException("Unsupported operation.");
    }

    /**
     * Enriches an input repository with data and algorithms provided by this class
     *
     * @param r the repository to enrich
     * @return the enriched repository
     */
    @Override
    public RepositoryInterface enrichRepository(RepositoryInterface r) {
        RepositoryInterface result = r;
        for (PathwayEnricherInterface er : enrichers) {
            result = er.init().setParameters(getParameters()).setSpecies(species).enrichRepository(result);
        }
        return result;
    }
}
