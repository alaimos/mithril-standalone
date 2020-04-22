package com.alaimos.MITHrIL.CommandLine.Threaded;

import com.alaimos.Commons.Algorithm.Threaded.Buffer;
import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.MITHrIL.Algorithm.PathwayMerger;
import com.alaimos.MITHrIL.Algorithm.Threaded.PathwayConsumerThread;
import com.alaimos.MITHrIL.CommandLine.Options.FastPathwayOptions;
import com.alaimos.MITHrIL.Common;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.ExpressionBatchReader;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Reader.TextReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Results.Threaded.FastPerturbationIO;
import com.alaimos.MITHrIL.Data.Writer.Threaded.FastPathwayWriterThread;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 05/01/2016
 */
public class FastPathwayService implements Service {

    protected Random random = new Random();

    protected FastPathwayOptions options = new FastPathwayOptions();

    @Override
    public String getShortName() {
        return "fast-pathway";
    }

    @Override
    public String getDescription() {
        return "runs Fast Batch Pathway Perturbation computation";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    private void report(String s) {
        System.out.print(s);
    }

    private void reportln(String s) {
        System.out.println(s);
    }

    private void reportln() {
        reportln("...OK!");
    }

    /**
     * Read pathway repository
     *
     * @param s species definition
     * @return the repository
     */
    protected RepositoryInterface getPathwayRepository(Species s) {
        return Common.getPathwayRepository(s, false, options.enrichmentEvidenceType, false, null, this::report);

    }

    @NotNull
    private RepositoryInterface buildMetaPathway(RepositoryInterface r) {
        var pm = new PathwayMerger();
        pm.init()
                .setParameter("repository", r)
                .setParameter("include", options.includeCategories)
                .setParameter("exclude", options.excludeCategories);
        if (options.includePathways != null) {
            if (!options.includePathways.exists())
                throw new InputParametersException("Invalid list of included pathways: file not found.");
            pm.setParameter("includePathways", new TextReader().read(options.includePathways));
        }
        if (options.excludePathways != null) {
            if (!options.excludePathways.exists())
                throw new InputParametersException("Invalid list of excluded pathways: file not found.");
            pm.setParameter("excludePathways", new TextReader().read(options.excludePathways));
        }
        pm.run();
        var res = pm.getOutput();
        pm.clear();
        res.setDefaultWeightComputation();
        return res;
    }

    /**
     * Waits for a group of threads to end
     *
     * @param tg A ThreadGroup object
     * @throws InterruptedException Exception thrown in case of interruption
     */
    private <T> void waitThreadGroup(@NotNull ThreadGroup tg, Buffer<T> buffer, Supplier<T> tSupplier) throws InterruptedException {
        if (tg.activeCount() > 0) {
            Thread[] activeThreads = new Thread[tg.activeCount()];
            tg.enumerate(activeThreads);
            for (Thread t : activeThreads) {
                if (t != null) {
                    var s = t.getState();
                    if (s != Thread.State.TERMINATED && s != Thread.State.NEW) {
                        if (buffer.isEmpty() && (s == Thread.State.WAITING || s == Thread.State.BLOCKED)) {
                            var count = 0;
                            while (s == Thread.State.WAITING || s == Thread.State.BLOCKED) {
                                buffer.closeAndPush(tSupplier.get());
                                t.join(10);
                                s = t.getState();
                                count++;
                                if (count > 100) {
                                    t.interrupt();
                                    break;
                                }
                            }
                        } else {
                            t.join();
                        }
                    }
                }
            }
        }
    }

    private void waitWriterThread(@NotNull Buffer<FastPerturbationIO> outputBuffer, FastPathwayWriterThread writerThread) throws Exception {
        outputBuffer.closeAndPush(new FastPerturbationIO());
        int count = 0;
        final long wait = 600000;
        while (writerThread != null && writerThread.isAlive() && count < 10) {
            writerThread.join(wait);
            writerThread.close();
            outputBuffer.closeAndPush(new FastPerturbationIO());
            count++;
        }
        if (writerThread != null && writerThread.isAlive()) {
            writerThread.interrupt();
        }
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
        if (!options.input.exists()) {
            throw new InputParametersException("Invalid input file: file does not exist.");
        }
        report("Reading species database");
        var speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
        var db = speciesDbReader.readSpecies();
        reportln();
        if (!db.containsKey(options.organism))
            throw new InputParametersException("Invalid species: species not found.");
        var s = db.get(options.organism);
        report("Reading pathways for " + s.getName());
        var r = getPathwayRepository(s);
        r.setDefaultWeightComputation();
        reportln();
        report("Merging pathways");
        r = buildMetaPathway(r);
        reportln();
        report("Reading expressions");
        var expressions = new ExpressionBatchReader().setFile(options.input).read();
        reportln();
        report("Initializing buffers...");
        var inputBuffer = new Buffer<FastPerturbationIO>(options.bufferSize);
        var outputBuffer = new Buffer<FastPerturbationIO>(options.bufferSize);
        report("Starting output thread...");
        FastPathwayWriterThread writerThread = null;
        try {
            writerThread = new FastPathwayWriterThread("Output-thread", outputBuffer, options.output, false);
            writerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        report("Starting processing threads...");
        final var sharedParams = new ConcurrentHashMap<String, Object>();
        sharedParams.put("repository", r);
        sharedParams.put("accumulator", options.accumulator);
        ProcessingThreadGroup ptg = new ProcessingThreadGroup();
        for (int i = 0; i < options.threads; i++) {
            var t = new PathwayConsumerThread(ptg, "Processing-Thread-" + i, inputBuffer, outputBuffer, sharedParams);
            t.start();
        }
        report("Filling buffers...");
        expressions.forEach((key, value) -> inputBuffer.set(new FastPerturbationIO(key, value)));
        report("Closing input buffer...");
        inputBuffer.close();
        try {
            report("Waiting end of computation...");
            waitThreadGroup(ptg, inputBuffer, FastPerturbationIO::new);
        } catch (InterruptedException ignore) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            report("Waiting output buffer...");
            waitWriterThread(outputBuffer, writerThread);
        } catch (InterruptedException ignore) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportln("Done!");
    }
}
