package com.alaimos.Commons.Algorithm.Pipelines;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class runs an algorithm with a batch of parameters
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 30/12/2015
 */
public class Batch<O> extends AbstractAlgorithm<Map<Integer, O>> {

    protected Class<? extends AbstractAlgorithm<O>> algorithmClass;
    protected BiFunction<Integer, O, O>             resultsPostProcessor = null;

    public Batch(Class<? extends AbstractAlgorithm<O>> algorithmClass) {
        this.algorithmClass = algorithmClass;
    }

    public Class<? extends AbstractAlgorithm<O>> getAlgorithmClass() {
        return algorithmClass;
    }

    public Batch<O> setAlgorithmClass(Class<? extends AbstractAlgorithm<O>> algorithmClass) {
        this.algorithmClass = algorithmClass;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Batch<O> putCommonParameter(String paramName, Object value) {
        if (!parameters.containsKey("common")) {
            parameters.put("common", new HashMap<>());
        }

        getParameterNotNull("common", Map.class).put(paramName, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Batch<O> addBatch(Map<String, Object> batch) {
        if (!parameters.containsKey("batch")) {
            parameters.put("batch", new ArrayList<>());
        }
        getParameterNotNull("batch", List.class).add(batch);
        return this;
    }

    public BiFunction<Integer, O, O> getResultsPostProcessor() {
        return resultsPostProcessor;
    }

    public void setResultsPostProcessor(BiFunction<Integer, O, O> resultsPostProcessor) {
        this.resultsPostProcessor = resultsPostProcessor;
    }

    /**
     * Clears this algorithm to free memory
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<Map<Integer, O>> clear() {
        super.clear();
        this.resultsPostProcessor = null;
        this.algorithmClass = null;
        return this;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        @SuppressWarnings("unchecked")
        var batchParameters = (List<Map<String, Object>>) getParameterNotNull("batch", List.class);
        @SuppressWarnings("unchecked")
        var commonParameters = (Map<String, Object>) getParameter("common", Map.class);
        var isParallel = !(getOptionalParameter("notParallel", Boolean.class).orElse(false));
        notifyObservers("batchStart");
        IntStream stream = IntStream.range(0, batchParameters.size());
        if (isParallel) stream = stream.parallel();
        output = stream.mapToObj(index -> {
            notifyObservers("batchStartingSingleAlgorithm", index);

            var parameters = batchParameters.get(index);
            if (commonParameters != null) parameters.putAll(commonParameters);

            var algorithm = createObject(algorithmClass);

            algorithm.init().setParameters(parameters).setObservers(getObservers());
            algorithm.run();

            notifyObservers("batchDoneSingleAlgorithm", index);

            O result = algorithm.getOutput();

            algorithm.clear();

            return new Pair<>(index, (resultsPostProcessor == null) ? result :
                                     resultsPostProcessor.apply(index, result));
        }).filter(f -> f.getSecond() != null).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        notifyObservers("batchDone");
    }
}
