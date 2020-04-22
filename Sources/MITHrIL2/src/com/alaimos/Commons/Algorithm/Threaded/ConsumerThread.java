package com.alaimos.Commons.Algorithm.Threaded;

import com.alaimos.Commons.Algorithm.AlgorithmExecutionException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConsumerThread<I, O> extends Thread implements Runnable, AutoCloseable {

    private final Buffer<I> inputBuffer;
    protected final ConcurrentHashMap<String, Object> sharedParameters;
    private final Buffer<O> outputBuffer;
    private boolean isClosed = false;

    public ConsumerThread(ThreadGroup g, String name, Buffer<I> inputBuffer, Buffer<O> outputBuffer,
                          ConcurrentHashMap<String, Object> sharedParameters) {
        super(g, name);
        this.inputBuffer = inputBuffer;
        this.sharedParameters = sharedParameters;
        this.outputBuffer = outputBuffer;

    }

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

    protected abstract O consume(I input);

    @Override
    public void run() {
        while (!inputBuffer.isClosed() || inputBuffer.size() > 0) {
            try {
                var in = inputBuffer.get();
                var out = consume(in);
                if (outputBuffer != null && out != null) outputBuffer.set(out);
            } catch (InterruptedException e) {
                break;
            }
        }
        try {
            close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isClosed() {
        return isClosed;
    }

    protected abstract void doClose();

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            this.doClose();
            isClosed = true;
        }
    }
}
