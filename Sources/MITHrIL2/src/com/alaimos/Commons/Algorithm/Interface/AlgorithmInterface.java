package com.alaimos.Commons.Algorithm.Interface;

import com.alaimos.Commons.Observer.Interface.ObservableInterface;

import java.util.Map;

/**
 * An algorithmClass which takes an input and returns a result.
 *
 * @param <O> The type of the output
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public interface AlgorithmInterface<O> extends Runnable, ObservableInterface {

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    AlgorithmInterface<O> init();

    /**
     * Set parameters for this algorithmClass
     *
     * @param parameters a map of parameters
     * @return This object for a fluent interface
     */
    AlgorithmInterface<O> setParameters(Map<String, Object> parameters);

    /**
     * Add a single parameter
     *
     * @param parameter the parameter name
     * @param value     the parameter value
     * @return This object for a fluent interface
     */
    AlgorithmInterface<O> setParameter(String parameter, Object value);

    /**
     * Clear algorithm parameters to save memory
     *
     * @return This object for a fluent interface
     */
    AlgorithmInterface<O> clear();

    /**
     * Get the output of this algorithmClass
     *
     * @return The output
     */
    O getOutput();

}