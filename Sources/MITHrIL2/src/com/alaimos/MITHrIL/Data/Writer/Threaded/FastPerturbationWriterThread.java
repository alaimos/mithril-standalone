package com.alaimos.MITHrIL.Data.Writer.Threaded;

import com.alaimos.Commons.Algorithm.Threaded.Buffer;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Results.Threaded.FastPerturbationIO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

public class FastPerturbationWriterThread extends Thread implements Runnable, AutoCloseable {

    private final Buffer<FastPerturbationIO> outputBuffer;
    private final PrintStream stream;
    private final PathwayInterface metaPathway;
    private List<String> headers = null;
    private boolean isClosed = false;

    public FastPerturbationWriterThread(String name, Buffer<FastPerturbationIO> outputBuffer, File outputFile,
                                        boolean isCompressed, PathwayInterface metaPathway) throws IOException {
        super(name);
        this.outputBuffer = outputBuffer;
        this.metaPathway = metaPathway;
        OutputStream os = new FileOutputStream(outputFile);
        if (isCompressed) {
            os = new GZIPOutputStream(os);
        }
        this.stream = new PrintStream(new BufferedOutputStream(os));
    }

    private void writeHeader() {
        stream.print("\t");
        stream.println(String.join("\t", headers));
        stream.print("#");
        var endpoints = metaPathway.getGraph().getEndpoints();
        stream.println(headers.stream().map(s -> (endpoints.contains(s) ? "Yes" : "No")).collect(Collectors.joining("\t")));
    }

    private void write(FastPerturbationIO res) {
        if (stream == null) throw new RuntimeException("Interrupted");
        if (headers == null) {
            assert res.value != null;
            headers = new ArrayList<>(res.value.keySet());
            writeHeader();
        }
        var map = res.value;
        assert map != null;
        var sb = new StringBuilder().append(res.name);
        for (var h : headers) {
            sb.append("\t").append(map.getOrDefault(h, 0d));
        }
        stream.println(sb.toString());
    }

    @Override
    public void run() {
        int max = outputBuffer.getMaxSize(), count = 0;
        while (!isClosed) {
            if (outputBuffer.closed() && outputBuffer.isEmpty()) break;
            if (!outputBuffer.closed() && outputBuffer.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    continue;
                }
            }
            try {
                var o = outputBuffer.get();
                if (o.empty && outputBuffer.closed()) break;
                if (o.empty && !outputBuffer.closed()) continue;
                write(o);
            } catch (InterruptedException e) {
                break;
            }
            if (++count > max) {
                stream.flush();
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

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            stream.close();
            isClosed = true;
        }
    }
}
