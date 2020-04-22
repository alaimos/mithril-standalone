package com.alaimos.Commons.Algorithm.Impl;

import com.alaimos.Commons.Algorithm.AlgorithmExecutionException;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Observer.ObserverImpl.Observable;
import com.alaimos.Commons.Utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract implementation of an algorithm
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public abstract class AbstractAlgorithm<O> extends Observable implements AlgorithmInterface<O> {

    protected Map<String, Object> parameters;
    protected O                   output;

    /**
     * Create an instance of an object from its class
     *
     * @param clazz the class of an object
     * @param <O>   the type of the object
     * @return the object
     */
    @NotNull
    protected static <O> O createObject(@NotNull Class<O> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new AlgorithmExecutionException("Unable to create an object of class " + clazz.getName(), e);
        }
    }

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<O> init() {
        parameters = new HashMap<>();
        output = null;
        return this;
    }

    /**
     * Set parameters for this algorithmClass
     *
     * @param parameters an hashmap of parameters
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<O> setParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
        notifyObservers("settingParameters", this.parameters);
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
    public AlgorithmInterface<O> setParameter(String parameter, Object value) {
        this.parameters.put(parameter, value);
        notifyObservers("settingParameters", this.parameters);
        return this;
    }

    /**
     * Clears this algorithm to free memory
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<O> clear() {
        this.parameters.clear();
        this.output = null;
        return this;
    }

    /**
     * Get a parameter
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
     * Internal method that get an optional parameter
     *
     * @param paramName    the name of the parameter
     * @param castTo       the class object used to cast the parameter
     * @param defaultValue the default value of the parameter
     * @param <E>          the formal class of the parameter
     * @return the actual value of the parameter
     */
    protected <E> E getParameter(String paramName, Class<E> castTo, E defaultValue) {
        E tmp = getParameter(paramName, castTo);
        if (tmp == null) tmp = defaultValue;
        return tmp;
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

    /**
     * Get the output of this algorithmClass
     *
     * @return The output
     */
    @Override
    public O getOutput() {
        return output;
    }
}
