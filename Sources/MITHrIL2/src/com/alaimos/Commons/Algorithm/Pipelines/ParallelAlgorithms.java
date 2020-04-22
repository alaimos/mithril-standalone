package com.alaimos.Commons.Algorithm.Pipelines;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Implementation of an algorithmClass consisting of multiple algorithms or pipelines running simultaneously.
 * Their output are then combined in order to obtain something that can be used for further computations.
 *
 * @param <R> the type of output of a single algorithmClass
 * @param <O> the type of the combined output
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public class ParallelAlgorithms<R, O> extends AbstractAlgorithm<O> {

    private List<AlgorithmInterface<R>>                 algorithms  = new ArrayList<>();
    private BiFunction<List<R>, Map<String, Object>, O> accumulator = null;

    public ParallelAlgorithms() {
    }

    public ParallelAlgorithms<R, O> addAlgorithm(AlgorithmInterface<R> p) {
        algorithms.add(p);
        return this;
    }

    public ParallelAlgorithms<R, O> clearAlgorithms() {
        algorithms.clear();
        return this;
    }

    public ParallelAlgorithms<R, O> setAccumulator(BiFunction<List<R>, Map<String, Object>, O> accumulator) {
        this.accumulator = accumulator;
        return this;
    }

    @Override
    public void run() {
        var isParallel = !(getOptionalParameter("notParallel", Boolean.class).orElse(false));
        notifyObservers("parallelPipelineStart");
        var stream = (isParallel) ? algorithms.parallelStream() : algorithms.stream();
        List<R> res = stream.map(p -> {
            notifyObservers("parallelPipelineStartBlock", p);
            p.init().setParameters(parameters).run();
            notifyObservers("parallelPipelineDoneBlock", p);
            var tmp = p.getOutput();
            p.clear();
            return tmp;
        }).collect(Collectors.toList());
        output = accumulator.apply(res, parameters);
        notifyObservers("parallelPipelineStop");
    }

}
