package com.alaimos.PHENSIM.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Algorithm.PathwayMerger;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Reader.TextReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.PHENSIM.Algorithm.PHENSIM;
import com.alaimos.PHENSIM.CommandLine.Observer.PHENSIMObserver;
import com.alaimos.PHENSIM.CommandLine.Options.PHENSIMOptions;
import com.alaimos.PHENSIM.Data.Reader.SIMPATHYInputReader;
import com.alaimos.PHENSIM.Data.Writer.PhensimMatrixWriter;
import com.alaimos.PHENSIM.Data.Writer.PhensimRunWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.alaimos.PHENSIM.Common.getEnrichedRepository;

/**
 * PHENSIM Command Line service
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PHENSIMService implements Service {

    protected PHENSIMOptions options = new PHENSIMOptions();

    @Override
    public String getShortName() {
        return "phensim";
    }

    @Override
    public String getDescription() {
        return "runs PHENSIM algorithm";
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
        return getEnrichedRepository(s, options.randomSeed, this::report, options.enrichers,
                options.enrichmentEvidenceType, options.enrichersParameters);
    }

    /**
     * Runs PHENSIM Computation
     */
    @Override
    public void run() {
        if (!options.input.exists()) {
            reportln("Invalid input file: file does not exist.");
            System.exit(101);
        }
        report("Reading species database");
        var speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
        var db = speciesDbReader.readSpecies();
        reportln("...OK!");
        if (!db.containsKey(options.organism)) {
            reportln("Invalid species: species not found.");
            System.exit(102);
        }
        try {
            var s = db.get(options.organism);
            report("Reading pathways for " + s.getName());
            var r = getPathwayRepository(s);
            r.setDefaultWeightComputation(options.weightComputationMethod);
            reportln("...OK!");
            report("Merging pathways");
            PathwayMerger pm = new PathwayMerger();
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
            r = pm.getOutput();
            r.setDefaultWeightComputation(options.weightComputationMethod);
            reportln("...OK!");
            report("Reading input");
            var constraints = new SIMPATHYInputReader().setFile(options.input).read();
            var simulateOn = new ArrayList<>(constraints.keySet());
            reportln("...OK!");
            if (options.nonExpressedFile != null) {
                if (!options.nonExpressedFile.exists())
                    throw new InputParametersException("Invalid list of non-expressed nodes: file not found");
                options.nonExpressedNodes = Arrays.asList(new TextReader().read(options.nonExpressedFile));
            }
            if (options.removeNodesFile != null) {
                report("Removing nodes");
                if (!options.removeNodesFile.exists())
                    throw new InputParametersException("Invalid list of removed nodes: file not found");
                var toRemove = new TextReader().read(options.removeNodesFile);
                for (var remove : toRemove) {
                    report("..." + remove);
                    r.removeNode(remove);
                }
                reportln("...OK!");
            }
            var simp = new PHENSIM();
            var rand = (options.randomSeed == null) ? new Random() : new Random(options.randomSeed);
            simp.init()
                    .setParameter("simulateOn", simulateOn)
                    .setParameter("constraints", constraints)
                    .setParameter("repository", r)
                    .setParameter("random", rand)
                    .setParameter("nonExpressedNodes", options.nonExpressedNodes)
                    .setParameter("numberOfRepetitions", options.iterations)
                    .setParameter("numberOfSimulations", options.simulations)
                    .setParameter("numberOfSimulationsFirst", options.simulationsFirst)
                    .setParameter("epsilon", options.epsilon)
                    .setParameter("threads", options.threads)
                    .addObserver(new PHENSIMObserver(this::report, this::reportln, options.verbose));
            simp.run();
            var output = simp.getOutput();
            report("Writing output");
            new PhensimRunWriter(options.significanceThreshold).write(options.output, output);
            reportln("...OK!");
            if (options.outputPathwayMatrix != null) {
                report("Writing pathway activity scores matrix");
                new PhensimMatrixWriter<String>().write(
                        options.outputPathwayMatrix,
                        output.makePathwayActivityScoresMatrix()
                );
                reportln("...OK!");
            }
            if (options.outputNodesMatrix != null) {
                report("Writing node activity scores matrix");
                new PhensimMatrixWriter<Pair<String, String>>(
                        p -> (p.getFirst().equals("metap")) ? p.getSecond() : p.toString()
                ).write(
                        options.outputNodesMatrix,
                        output.makeNodesActivityScoresMatrix()
                );
                reportln("...OK!");
            }
        } catch (Exception e) {
            System.out.println("\n" + e.getMessage());
            System.exit(103);
        }
    }
}
