package com.alaimos.Commons.Algorithm.Pipelines;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a pipeline of algorithms whose outputs are mapped into the next input using a special adapter
 * object.
 *
 * @param <R> the type of the final output
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public class AlgorithmInterface<R> extends AbstractAlgorithm<R> {

    private List<AbstractAlgorithm> blocks = new ArrayList<>();
    private List<Pipe<Object>>      pipes  = new ArrayList<>();

    public AlgorithmInterface() {
    }

    public AlgorithmInterface<R> addBlock(AbstractAlgorithm workflow) {
        var isEmpty = blocks.isEmpty();
        blocks.add(workflow);
        if (!isEmpty) {
            pipes.add(null);
        }
        return this;
    }

    public AlgorithmInterface<R> addBlock(AbstractAlgorithm workflow, Pipe<Object> pipe) {
        var isEmpty = blocks.isEmpty();
        blocks.add(workflow);
        if (!isEmpty) {
            pipes.add(pipe);
        }
        return this;
    }

    public AlgorithmInterface<R> clearBlocks() {
        blocks.clear();
        pipes.clear();
        return this;
    }

    public AlgorithmInterface<R> setPipe(Pipe<Object> pipe, int step) {
        if (step < 0 || step > (blocks.size() - 1)) {
            throw new RuntimeException("Invalid step number");
        }
        pipes.set(step, pipe);
        return this;
    }

    public AlgorithmInterface<R> clearPipes() {
        for (int i = 0; i < pipes.size(); i++) {
            pipes.set(i, null);
        }
        return this;
    }

    /**
     * Clears this algorithm to free memory
     *
     * @return This object for a fluent interface
     */
    @Override
    public com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface<R> clear() {
        super.clear();
        return clearBlocks();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        Object tmpResult = null;
        Map<String, Object> params = null;
        notifyObservers("pipelineStart");

        for (int i = 0; i < blocks.size(); i++) {
            notifyObservers("pipelineStartBlock", i);

            if (i == 0) {
                params = parameters;
            } else {
                Pipe<Object> c = pipes.get(i - 1);
                if (c != null) {
                    params = c.apply(tmpResult, params);
                }
            }

            var w = blocks.get(i);
            w.init().setParameters(params).run();
            tmpResult = w.getOutput();
            w.clear();
            notifyObservers("pipelineDoneBlock", i);
        }

        output = (R) tmpResult;
        notifyObservers("pipelineDone");
    }
}
