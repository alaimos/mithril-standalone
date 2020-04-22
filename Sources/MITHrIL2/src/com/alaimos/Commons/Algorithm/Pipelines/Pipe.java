package com.alaimos.Commons.Algorithm.Pipelines;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Takes the result of an algorithmClass and prepares it for the next block of the pipeline
 *
 * @param <R> The type of the output of the previous computation
 * @author Alaimo Salvatore
 * @version 2.0
 */
@FunctionalInterface
public interface Pipe<R> extends BiFunction<R, Map<String, Object>, Map<String, Object>> {
}