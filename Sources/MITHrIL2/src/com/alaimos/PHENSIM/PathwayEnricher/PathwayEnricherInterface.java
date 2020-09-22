package com.alaimos.PHENSIM.PathwayEnricher;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import com.alaimos.MITHrIL.Algorithm.RepositoryEnricher;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Records.Species;

import java.util.Map;

/**
 * This interface provides the standard structure of pathway repository enrichment
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public interface PathwayEnricherInterface {

    /**
     * Get short name that identify this enricher
     *
     * @return a short unique name
     */
    String getShortName();

    /**
     * Get a description for this enricher
     *
     * @return a description
     */
    String getDescription();

    /**
     * This method instantiate an algorithm which enriches repositories
     *
     * @return an algorithm which enriches pathways
     */
    default AbstractAlgorithm<RepositoryInterface> getRepositoryEnricher() {
        return new RepositoryEnricher();
    }

    /**
     * Prepares this object for a new execution
     *
     * @return this object for a fluent interface
     */
    PathwayEnricherInterface init();

    /**
     * Clear all parameters and add new ones
     *
     * @param parameters a map of parameters
     * @return this object for a fluent interface
     */
    default PathwayEnricherInterface setParameters(Map<String, Object> parameters) {
        return clearParameters().addParameters(parameters);
    }

    /**
     * Clear all parameters and add a new one
     *
     * @param name  the name of a parameter
     * @param value the value of a parameter
     * @return this object for a fluent interface
     */
    default PathwayEnricherInterface setParameter(String name, Object value) {
        return clearParameters().addParameter(name, value);
    }

    /**
     * Adds parameters to this object
     *
     * @param parameters a map of parameters
     * @return this object for a fluent interface
     */
    PathwayEnricherInterface addParameters(Map<String, Object> parameters);

    /**
     * Add a parameter to this object
     *
     * @param name  the name of a parameter
     * @param value the value of a parameter
     * @return this object for a fluent interface
     */
    PathwayEnricherInterface addParameter(String name, Object value);

    /**
     * Clear all parameters provided to this object
     *
     * @return this object for a fluent interface
     */
    PathwayEnricherInterface clearParameters();

    /**
     * Gets all the parameters provided to this object
     *
     * @return the parameters of this object
     */
    Map<String, Object> getParameters();

    /**
     * Returns the object containing data on the pathway species.
     *
     * @return the species
     */
    Species getSpecies();

    /**
     * Sets the object containing data on the pathway species.
     *
     * @param species the species
     * @return this object for a fluent interface
     */
    PathwayEnricherInterface setSpecies(Species species);

    /**
     * Builds and returns the data structures which contains the data that will be used to enrich repositories.
     *
     * @return the data to use to enrich a repository
     */
    RepositoryEnrichmentInterface getRepositoryEnrichment();

    /**
     * Enriches an input repository with data and algorithms provided by this class
     *
     * @param r the repository to enrich
     * @return the enriched repository
     */
    default RepositoryInterface enrichRepository(RepositoryInterface r) {
        RepositoryEnrichmentInterface rei = getRepositoryEnrichment();
        if (rei == null) return r;
        AbstractAlgorithm<RepositoryInterface> re = getRepositoryEnricher();
        re.init();
        re.setParameter("repository", r).setParameter("enrichment", rei).run();
        return re.getOutput();
    }

}
