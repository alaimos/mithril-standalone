package com.alaimos.MITHrIL.CommandLine;

import com.alaimos.Commons.Algorithm.Pipelines.VoidBatch;
import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.Commons.Math.PValue.Adjusters;
import com.alaimos.Commons.Math.PValue.Combiners;
import com.alaimos.MITHrIL.Algorithm.MITHrIL;
import com.alaimos.MITHrIL.CommandLine.Observer.MITHrILObserver;
import com.alaimos.MITHrIL.CommandLine.Options.MITHrILBatchOptions;
import com.alaimos.MITHrIL.Common;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.ExpressionBatchReader;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;
import com.alaimos.MITHrIL.Data.Writer.BinaryWriter;
import com.alaimos.MITHrIL.Data.Writer.MITHrIL.MainOutputWriter;
import com.alaimos.MITHrIL.Data.Writer.MITHrIL.NodesOutputWriter;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 05/01/2016
 */
public class BatchMITHrILService implements Service {

    protected MITHrILBatchOptions options = new MITHrILBatchOptions();

    @Override
    public String getShortName() {
        return "batch-mithril";
    }

    @Override
    public String getDescription() {
        return "runs MITHrIL 2 algorithm on a batch of log-fold-changes";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    public synchronized void report(String s) {
        if (options.verbose) System.out.print(s);
    }

    public synchronized void reportln(String s) {
        if (options.verbose) System.out.println(s);
    }

    /**
     * Read pathway repository
     *
     * @param s species definition
     * @return the repository
     */
    protected RepositoryInterface getPathwayRepository(Species s) {
        return Common.getPathwayRepository(s, options.noEnrichment, options.enrichmentEvidenceType, options.decoys, options.randomSeed, this::report);

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
        if (!options.outputDir.isDirectory()) {
            throw new InputParametersException("Invalid output directory: directory does not exist.");
        }
        report("Reading species database");
        var speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
        var db = speciesDbReader.readSpecies();
        reportln("...OK!");
        if (!db.containsKey(options.organism)) throw new InputParametersException("Invalid species: species not found.");
        var s = db.get(options.organism);
        report("Reading pathways for " + s.getName());
        var r = getPathwayRepository(s);
        r.setDefaultWeightComputation(options.weightComputationMethod);
        reportln("...OK!");
        if (options.metaPathway) {
            report("Merging pathways");
            r = MITHrILService.buildMetaPathway(r, options);
            reportln("...OK!");
        }
        report("Reading expressions");
        var expressions = new ExpressionBatchReader().setFile(options.input).read();
        if (!options.noCompletePathway) {
            report("...Completing pathways");
            var e = expressions.entrySet().iterator().next();
            if (e != null) r.completePathways(e.getValue());
        }
        reportln("...OK!");
        var rand = (options.randomSeed == null) ? new Random() : new Random(options.randomSeed);
        var batchMap = new HashMap<Integer, String>();
        report("Preparing batches");
        var batch = new VoidBatch<>(MITHrIL.class, options.threads);
        batch.init().addObserver(new MITHrILObserver(this::report, this::reportln));
        batch.putCommonParameter("repository", r)
             .putCommonParameter("random", rand)
             .putCommonParameter("numberOfRepetitions", options.pValueIterations)
             .putCommonParameter("pValueCombiner", Combiners.getByName(options.pValueCombiner))
             .putCommonParameter("pValueAdjuster", Adjusters.getByName(options.pValueAdjuster));
        int i = 0;
        for (var e : expressions.entrySet()) {
            batchMap.put(i, e.getKey());
            var tmp = new HashMap<String, Object>();
            tmp.put("expressions", e.getValue());
            batch.addBatch(tmp);
            ++i;
        }
        final var repo = r;
        batch.setResultsConsumer((idx, result) -> {
            report("Writing output");
            String name = batchMap.get(idx);
            if (name == null) throw new RuntimeException("Something went wrong!");
            File mainOut = new File(options.outputDir, name + ".main.txt"),
                    nodesOut = new File(options.outputDir, name + ".nodes.txt"),
                    endpointsOut = new File(options.outputDir, name + ".endpoints.txt");
            new MainOutputWriter(repo).write(mainOut, result);
            new NodesOutputWriter(repo).write(nodesOut, result);
            new NodesOutputWriter(repo, true).write(endpointsOut, result);
            if (options.binaryOutput) {
                File out = new File(options.outputDir, name + ".output.dat");
                new BinaryWriter<PathwayAnalysisResult>().write(out, result);
            }
            reportln("...OK!");
        });
        reportln("...OK!");
        batch.run();
    }
}
