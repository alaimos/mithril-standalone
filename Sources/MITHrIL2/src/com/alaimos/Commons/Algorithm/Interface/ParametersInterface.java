package com.alaimos.Commons.Algorithm.Interface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * @param <T> A generic type used to bind Parameter Interface to a specific Algorithm
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 21/04/16
 */
public interface ParametersInterface<T extends AlgorithmInterface<?>> {

    /**
     * Set parameters for this algorithmClass
     *
     * @param parameters an hashmap of parameters
     * @return This object for a fluent interface
     */
    ParametersInterface<T> setParameters(Map<String, Object> parameters);

    /**
     * Add a single parameter
     *
     * @param parameter the parameter name
     * @param value     the parameter value
     * @return This object for a fluent interface
     */
    ParametersInterface<T> setParameter(String parameter, Object value);

    /**
     * Clears all parameters
     *
     * @return This object for a fluent interface
     */
    ParametersInterface<T> clearParameters();

    /**
     * Get a parameter
     *
     * @param paramName the parameter
     * @return the value of the parameter
     */
    @Nullable
    Object getParameter(String paramName);

    /**
     * Internal method that get a parameter
     *
     * @param paramName the name of the parameter
     * @param castTo    the class object used to cast the parameter
     * @param <E>       the formal class of the parameter
     * @return the value of the parameter
     */
    <E> E getParameter(String paramName, Class<E> castTo);

    /**
     * Internal method that gets an optional parameter
     *
     * @param paramName the name of the parameter
     * @param castTo    the class object used to cast the parameter
     * @param <E>       the formal class of the parameter
     * @return An optional value for the parameter
     */
    <E> Optional<E> getOptionalParameter(String paramName, Class<E> castTo);

    /**
     * Internal method that get a parameter
     *
     * @param paramName the name of the parameter
     * @param castTo    the class of the parameter
     * @param <E>       the class of the parameter
     * @return the value of the parameter
     */
    @NotNull
    <E> E getParameterNotNull(String paramName, Class<E> castTo);

    /**
     * Get all parameters in a map.
     * <p>
     * The key of the resulting hashmap is a string with the name of the parameter, while the value is an object with
     * the parameter value itself.
     *
     * @return a map of parameters
     */
    Map<String, Object> getParameters();


}
