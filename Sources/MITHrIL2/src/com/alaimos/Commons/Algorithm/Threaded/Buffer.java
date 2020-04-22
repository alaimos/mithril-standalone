package com.alaimos.Commons.Algorithm.Threaded;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Buffer<T> {

    private final BlockingQueue<T> buffer;
    private boolean isClosed = false;
    private int maxSize;

    /**
     * Class constructor
     *
     * @param maxSize The maximum buffer size
     */
    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new ArrayBlockingQueue<>(maxSize, true);
    }

    public int getMaxSize() {
        return maxSize;
    }

    private void push(T data) {
        try {
            buffer.put(data);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(T data) {
        if (isClosed) return;
        push(data);
    }

    public T get() throws InterruptedException {
        return buffer.take();
    }

    public void closeAndPush(T data) {
        close();
        push(data);
    }

    public void close() {
        isClosed = true;
    }

    public int size() {
        return buffer.size();
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public boolean closed() {
        return isClosed;
    }

    public boolean isClosed() {
        return isClosed && buffer.isEmpty();
    }
}
