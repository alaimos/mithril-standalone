package com.alaimos.MITHrIL.Algorithm.Threaded;

import com.alaimos.Commons.Algorithm.Threaded.Buffer;
import com.alaimos.Commons.Algorithm.Threaded.ConsumerThread;
import com.alaimos.MITHrIL.Data.Results.Threaded.FastPerturbationIO;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class PerturbationConsumerThread extends ConsumerThread<FastPerturbationIO, FastPerturbationIO> {

    private final ConcurrentFastPerturbation algorithm;

    public PerturbationConsumerThread(ThreadGroup g, String name, Buffer<FastPerturbationIO> inputBuffer,
                                      Buffer<FastPerturbationIO> outputBuffer, ConcurrentHashMap<String, Object> sharedParameters) {
        super(g, name, inputBuffer, outputBuffer, sharedParameters);
        this.algorithm = new ConcurrentFastPerturbation(sharedParameters);
        this.algorithm.init();
    }

    @Override
    protected FastPerturbationIO consume(@NotNull FastPerturbationIO input) {
        if (input.empty) return null;
        algorithm.clear();
        algorithm.setInput(input.value);
        algorithm.run();
        return new FastPerturbationIO("" + input.name, algorithm.getOutput());
    }

    @Override
    protected void doClose() {
        algorithm.clear();
    }
}
