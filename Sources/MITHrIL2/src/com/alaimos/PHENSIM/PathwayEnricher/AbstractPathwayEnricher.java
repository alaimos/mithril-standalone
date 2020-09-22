package com.alaimos.PHENSIM.PathwayEnricher;

import com.alaimos.Commons.Algorithm.AlgorithmExecutionException;
import com.alaimos.Commons.Observer.Interface.ObserverInterface;
import com.alaimos.Commons.Utils.Utils;
import com.alaimos.MITHrIL.Data.Records.Species;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This abstract class provides the standard implementation of pathway repository enrichment
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public abstract class AbstractPathwayEnricher implements PathwayEnricherInterface {

    protected Map<String, Object> parameters = new HashMap<>();

    protected Species species = null;

    public ObserverInterface observer;

    /**
     * Prepares this object for a new execution
     *
     * @return this object for a fluent interface
     */
    @Override
    public PathwayEnricherInterface init() {
        parameters = new HashMap<>();
        species = null;
        return this;
    }

    /**
     * Returns the object containing data on the pathway species.
     *
     * @return the species
     */
    @Override
    public Species getSpecies() {
        return this.species;
    }

    /**
     * Sets the object containing data on the pathway species.
     *
     * @param species the species
     * @return this object for a fluent interface
     */
    @Override
    public PathwayEnricherInterface setSpecies(Species species) {
        this.species = species;
        return this;
    }

    /**
     * Adds parameters to this object
     *
     * @param parameters a map of parameters
     * @return this object for a fluent interface
     */
    @Override
    public PathwayEnricherInterface addParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    /**
     * Add a parameter to this object
     *
     * @param name  the name of a parameter
     * @param value the value of a parameter
     * @return this object for a fluent interface
     */
    @Override
    public PathwayEnricherInterface addParameter(String name, Object value) {
        this.parameters.put(name, value);
        return this;
    }

    /**
     * Clear all parameters provided to this object
     *
     * @return this object for a fluent interface
     */
    @Override
    public PathwayEnricherInterface clearParameters() {
        this.parameters.clear();
        return this;
    }

    /**
     * Gets all the parameters provided to this object
     *
     * @return the parameters of this object
     */
    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    /**
     * Internal method that get a parameter
     *
     * @param paramName the parameter
     * @return the value of the parameter
     */
    @Nullable
    protected Object getParameter(String paramName) {
        return parameters.get(paramName);
    }

    /**
     * Internal method that get a parameter
     *
     * @param paramName the name of the parameter
     * @param castTo    the class object used to cast the parameter
     * @param <E>       the formal class of the parameter
     * @return the value of the parameter
     */
    @Nullable
    protected <E> E getParameter(String paramName, Class<E> castTo) {
        return Utils.checkedCast(getParameter(paramName), castTo);
    }

    /**
     * Internal method that gets an optional parameter
     *
     * @param paramName the name of the parameter
     * @param castTo    the class object used to cast the parameter
     * @param <E>       the formal class of the parameter
     * @return An optional value for the parameter
     */
    protected <E> Optional<E> getOptionalParameter(String paramName, Class<E> castTo) {
        return Optional.ofNullable(getParameter(paramName, castTo));
    }

    /**
     * Internal method that get a parameter
     *
     * @param paramName the name of the parameter
     * @param castTo    the class of the parameter
     * @param <E>       the class of the parameter
     * @return the value of the parameter
     */
    @NotNull
    protected <E> E getParameterNotNull(String paramName, Class<E> castTo) {
        E value = getParameter(paramName, castTo);
        if (value == null) {
            throw new AlgorithmExecutionException("The \"" + paramName + "\" parameter is required.");
        }
        return value;
    }

}
