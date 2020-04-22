package com.alaimos.MITHrIL.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.Commons.Math.PValue.Adjusters;
import com.alaimos.Commons.Math.PValue.Combiners;
import com.alaimos.MITHrIL.Algorithm.MITHrIL;
import com.alaimos.MITHrIL.Algorithm.PathwayMerger;
import com.alaimos.MITHrIL.CommandLine.Observer.MITHrILObserver;
import com.alaimos.MITHrIL.CommandLine.Options.MITHrILCommonOptions;
import com.alaimos.MITHrIL.CommandLine.Options.MITHrILOptions;
import com.alaimos.MITHrIL.Common;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.ExpressionMapReader;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Reader.TextReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;
import com.alaimos.MITHrIL.Data.Writer.BinaryWriter;
import com.alaimos.MITHrIL.Data.Writer.MITHrIL.MainOutputWriter;
import com.alaimos.MITHrIL.Data.Writer.MITHrIL.NodesOutputWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class MITHrILService implements Service {

    protected MITHrILOptions options = new MITHrILOptions();

    @Override
    public String getShortName() {
        return "mithril";
    }

    @Override
    public String getDescription() {
        return "runs MITHrIL 2 algorithm on a sample";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    public void report(String s) {
        if (options.verbose) System.out.print(s);
    }

    public void reportln(String s) {
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

    static RepositoryInterface buildMetaPathway(RepositoryInterface r, @NotNull MITHrILCommonOptions options) {
        var pm = new PathwayMerger();
        pm.init()
          .setParameter("repository", r)
          .setParameter("include", options.includeCategories)
          .setParameter("exclude", options.excludeCategories);
        if (options.includePathways != null) {
            if (!options.includePathways.exists()) throw new InputParametersException("Invalid list of included pathways: file not found.");
            pm.setParameter("includePathways", new TextReader().read(options.includePathways));
        }
        if (options.excludePathways != null) {
            if (!options.excludePathways.exists()) throw new InputParametersException("Invalid list of excluded pathways: file not found.");
            pm.setParameter("excludePathways", new TextReader().read(options.excludePathways));
        }
        pm.run();
        var res = pm.getOutput();
        pm.clear();
        res.setDefaultWeightComputation(options.weightComputationMethod);
        return res;
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
        if (!options.input.exists()) throw new InputParametersException("Invalid input file: file does not exist.");
        report("Reading species database");
        var speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
        var db = speciesDbReader.readSpecies();
        reportln("...OK!");
        if (!db.containsKey(options.organism)) {
            throw new InputParametersException("Invalid species: species not found.");
        }
        var s = db.get(options.organism);
        report("Reading pathways for " + s.getName());
        var r = getPathwayRepository(s);
        r.setDefaultWeightComputation(options.weightComputationMethod);
        reportln("...OK!");
        if (options.metaPathway) {
            report("Merging pathways");
            r = buildMetaPathway(r, options);
            reportln("...OK!");
        }
        report("Reading expressions");
        var expressions = new ExpressionMapReader().setFile(options.input).read();
        if (!options.noCompletePathway) {
            report("...Completing pathways");
            r.completePathways(expressions);
        }
        reportln("...OK!");
        var comp = new MITHrIL();
        Random rand = (options.randomSeed == null) ? new Random() : new Random(options.randomSeed);
        comp.init()
            .setParameter("expressions", expressions).setParameter("repository", r)
            .setParameter("random", rand).setParameter("numberOfRepetitions", options.pValueIterations)
            .setParameter("pValueCombiner", Combiners.getByName(options.pValueCombiner))
            .setParameter("pValueAdjuster", Adjusters.getByName(options.pValueAdjuster))
            .addObserver(new MITHrILObserver(this::report, this::reportln));
        comp.run();
        PathwayAnalysisResult results = comp.getOutput();
        report("Writing output");
        if (options.output != null) new MainOutputWriter(r).write(options.output, results);
        if (options.endpointsOutput != null) new NodesOutputWriter(r, true).write(options.endpointsOutput, results);
        if (options.perturbationsOutput != null) new NodesOutputWriter(r).write(options.perturbationsOutput, results);
        if (options.binaryOutput != null) new BinaryWriter<PathwayAnalysisResult>().write(options.binaryOutput, results);
        reportln("...OK!");
    }
}
