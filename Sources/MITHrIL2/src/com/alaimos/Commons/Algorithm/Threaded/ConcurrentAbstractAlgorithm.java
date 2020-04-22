package com.alaimos.Commons.Algorithm.Threaded;

import com.alaimos.Commons.Algorithm.AlgorithmExecutionException;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Observer.ObserverImpl.Observable;
import com.alaimos.Commons.Utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConcurrentAbstractAlgorithm<I, O> extends Observable implements AlgorithmInterface<O> {

    protected final ConcurrentHashMap<String, Object> parameters;
    protected I input;
    protected O output;

    public ConcurrentAbstractAlgorithm(ConcurrentHashMap<String, Object> sharedParameters) {
        this.parameters = sharedParameters;
    }

    public void setInput(I input) {
        this.input = input;
    }

    public I getInput() {
        return input;
    }

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<O> init() {
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
        return this;
    }

    /**
     * Clears this algorithm to free memory
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<O> clear() {
        this.input = null;
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
