package com.alaimos.Commons.Algorithm.Impl;

import com.alaimos.Commons.Algorithm.AlgorithmExecutionException;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Algorithm.Interface.ParametersInterface;
import com.alaimos.Commons.Utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A container of parameters
 *
 * @param <T> A generic type used to bind Parameter Interface to a specific Algorithm
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 21/04/16
 */
public abstract class AbstractParameters<T extends AlgorithmInterface<?>> implements ParametersInterface<T> {

    protected Map<String, Object> parameters = new HashMap<>();


    public AbstractParameters() {
    }

    /**
     * An array of allowed parameters
     *
     * @return an array of parameters name
     */
    protected String[] allowedParameters() {
        return null;
    }

    /**
     * Is a parameter allowed?
     *
     * @param param the parameter name
     * @return Is parameter allowed?
     */
    protected boolean isParameterAllowed(String param) {
        var allowed = allowedParameters();
        return allowed == null || Arrays.asList(allowed).contains(param);
    }

    /**
     * Get all parameters in a map.
     * <p>
     * The key of the resulting hashmap is a string with the name of the parameter, while the value is an object with
     * the parameter value itself.
     *
     * @return a map of parameters
     */
    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Set parameters for this algorithmClass
     *
     * @param parameters an hashmap of parameters
     * @return This object for a fluent interface
     */
    @Override
    public AbstractParameters<T> setParameters(Map<String, Object> parameters) {
        parameters.forEach((s, o) -> {
            if (isParameterAllowed(s)) {
                this.parameters.put(s, o);
            }
        });
        return this;
    }

    /**
     * Add a single parameter
     *
     * @param parameter the parameter name
     * @param value     the parameter value
     * @return This object for a fluent interface
     */
    @Override
    public AbstractParameters<T> setParameter(String parameter, Object value) {
        if (isParameterAllowed(parameter)) {
            this.parameters.put(parameter, value);
        }
        return this;
    }

    /**
     * Clears all parameters
     *
     * @return This object for a fluent interface
     */
    @Override
    public AbstractParameters<T> clearParameters() {
        this.parameters.clear();
        return this;
    }

    /**
     * Get a parameter
     *
     * @param paramName the parameter
     * @return the value of the parameter
     */
    @Nullable
    public Object getParameter(String paramName) {
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
    public <E> E getParameter(String paramName, Class<E> castTo) {
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
    public <E> Optional<E> getOptionalParameter(String paramName, Class<E> castTo) {
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
    public <E> E getParameterNotNull(String paramName, Class<E> castTo) {
        E value = getParameter(paramName, castTo);
        if (value == null) {
            throw new AlgorithmExecutionException("The \"" + paramName + "\" parameter is required.");
        }
        return value;
    }


}
