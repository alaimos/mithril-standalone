package com.alaimos.Commons.Algorithm.Pipelines;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * This class runs an algorithm with a batch of parameters
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 30/12/2015
 */
public class VoidBatch<O> extends AbstractAlgorithm<O> {

    private Class<? extends AbstractAlgorithm<O>> algorithmClass;
    private BiConsumer<Integer, O>                resultsConsumer = null;
    private int                                   threads         = 0;

    public VoidBatch(Class<? extends AbstractAlgorithm<O>> algorithmClass) {
        this.algorithmClass = algorithmClass;
    }

    public VoidBatch(Class<? extends AbstractAlgorithm<O>> algorithmClass, int threads) {
        this(algorithmClass);
        this.threads = threads;
    }

    public Class<? extends AbstractAlgorithm<O>> getAlgorithmClass() {
        return algorithmClass;
    }

    public VoidBatch<O> setAlgorithmClass(Class<? extends AbstractAlgorithm<O>> algorithmClass) {
        this.algorithmClass = algorithmClass;
        return this;
    }

    @SuppressWarnings("unchecked")
    public VoidBatch<O> putCommonParameter(String paramName, Object value) {
        if (!parameters.containsKey("common")) {
            parameters.put("common", new HashMap<>());
        }

        getParameterNotNull("common", Map.class).put(paramName, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public VoidBatch<O> addBatch(Map<String, Object> batch) {
        if (!parameters.containsKey("batch")) {
            parameters.put("batch", new ArrayList<>());
        }
        getParameterNotNull("batch", List.class).add(batch);
        return this;
    }

    /**
     * An expression that consumes object produced in the batch
     *
     * @return a consumer
     */
    public BiConsumer<Integer, O> getResultsConsumer() {
        return resultsConsumer;
    }

    /**
     * Set the expression that consumes objects produced by this batch
     *
     * @param resultsConsumer a consumer
     */
    public void setResultsConsumer(BiConsumer<Integer, O> resultsConsumer) {
        this.resultsConsumer = resultsConsumer;
    }

    @NotNull
    private IntConsumer singleIteration(List<Map<String, Object>> batchParameters, Map<String, Object> commonParameters) {
        return (index) -> {
            notifyObservers("batchStartingSingleAlgorithm", index);

            var parameters = batchParameters.get(index);
            if (commonParameters != null) parameters.putAll(commonParameters);

            var algorithm = createObject(algorithmClass);
            algorithm.init().setParameters(parameters).setObservers(getObservers());
            algorithm.run();

            notifyObservers("batchDoneSingleAlgorithm", index);

            if (resultsConsumer != null) {
                resultsConsumer.accept(index, algorithm.getOutput());
            }

            algorithm.clear();

        };
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
        var singleIteration = singleIteration(batchParameters, commonParameters);
        if (isParallel) {
            if (threads <= 0) threads = Runtime.getRuntime().availableProcessors();
            ForkJoinPool forkJoinPool = null;
            try {
                forkJoinPool = new ForkJoinPool(threads);
                forkJoinPool.submit(() -> {
                    IntStream.range(0, batchParameters.size()).parallel().forEach(singleIteration);
                }).get(); //this makes it an overall blocking call
            } catch (InterruptedException | ExecutionException ignored) {
            } finally {
                if (forkJoinPool != null) {
                    forkJoinPool.shutdown(); //always remember to shutdown the pool
                }
            }
        } else {
            for (int i = 0; i < batchParameters.size(); i++) {
                singleIteration.accept(i);
            }
        }
        notifyObservers("batchDone");
    }
}
