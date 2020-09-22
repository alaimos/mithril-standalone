package com.alaimos.PHENSIM.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.MITHrIL.Algorithm.PathwayMerger;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Reader.TextReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.PHENSIM.CommandLine.Options.ExportSubGraphOptions;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static com.alaimos.PHENSIM.Common.getEnrichedRepository;

/**
 * PHENSIM Command Line service
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class ExportSubGraphService implements Service {

    protected ExportSubGraphOptions options = new ExportSubGraphOptions();

    @Override
    public String getShortName() {
        return "export-subgraph";
    }

    @Override
    public String getDescription() {
        return "export a list of all downstream nodes given a set of input nodes";
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
        return getEnrichedRepository(s, null, this::report, options.enrichers,
                options.enrichmentEvidenceType, options.enrichersParameters);
    }

    /**
     * Runs PHENSIM Computation
     */
    @Override
    public void run() {
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
            reportln("...OK!");
            try (var writer = new PrintWriter(new FileWriter(options.output))) {
                var c = new ArrayList<NodeInterface>();
                report("Exporting sub-graphs");
                for (var p : r) {
                    var id = p.getId();
                    var graph = p.getGraph();
                    var genes = Arrays.stream(options.inputNodes).filter(graph::hasNode).toArray(String[]::new);
                    var tmp = new HashSet<>(Arrays.asList(genes));
                    for (var g : genes) {
                        c.clear();
                        graph.traverseDownstream(c, graph.getNode(g), true);
                        c.forEach(n -> tmp.add(n.getId()));
                    }
                    for (var g : tmp) {
                        writer.println(id + "\t" + g);
                    }
                }
                reportln("...OK!");
            }
        } catch (Exception e) {
            System.out.println("\n" + e.getMessage());
            System.exit(103);
        }
    }
}
