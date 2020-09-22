package com.alaimos.SPECifIC.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.MITHrIL.Common;
import com.alaimos.MITHrIL.Data.Pathway.Impl.MergedRepository;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.SPECifIC.CommandLine.Options.ExportGraphOptions;
import com.alaimos.SPECifIC.Data.Writer.EdgesIndexWriter;
import com.alaimos.SPECifIC.Data.Writer.NodesIndexWriter;
import com.alaimos.SPECifIC.Data.Writer.PathwayToEdgesIndexWriter;

import java.util.HashMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class ExportGraphService implements Service {

    protected ExportGraphOptions options = new ExportGraphOptions();

    @Override
    public String getShortName() {
        return "exportgraph";
    }

    @Override
    public String getDescription() {
        return "export pathway graph.";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    public synchronized void report(String s) {
        if (options.isVerbose()) {
            System.out.print(s);
        }
    }

    public synchronized void report(Exception e) {
        if (options.isVerbose()) {
            e.printStackTrace();
        }
    }

    public synchronized void reportln(String s) {
        report(s + "\n");
    }

    /**
     * Read pathway repository
     *
     * @param s species definition
     * @return the repository
     */
    protected RepositoryInterface getPathwayRepository(Species s) {
        return Common.getPathwayRepository(s, false, options.getEnrichmentEvidenceType(), false,
                null, this::report);
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
        report("Reading species database");
        RemoteSpeciesDatabaseReader speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
        HashMap<String, Species> db = speciesDbReader.readSpecies();
        reportln("...OK!");
        if (!db.containsKey(options.getOrganism())) {
            throw new InputParametersException("Invalid species: species not found.");
        }
        Species s = db.get(options.getOrganism());
        report("Reading pathways for " + s.getName());
        RepositoryInterface r = getPathwayRepository(s);
        r.setDefaultWeightComputation();
        reportln("...OK!");
        report("Merging pathways");
        MergedRepository rm =
                Common.mergeRepositories(r, options.getIncludeCategories(), options.getExcludeCategories(), null, null,
                        options.isDisablePriority());
        rm.setDefaultWeightComputation();
        PathwayInterface mp = rm.getPathway();
        reportln("...OK!");
        if (options.getNodesOutput() != null) {
            report("Exporting nodes index");
            new NodesIndexWriter().write(options.getNodesOutput(), mp);
            reportln("...OK!");
        }
        if (options.getEdgesOutput() != null) {
            report("Exporting edges index");
            new EdgesIndexWriter().write(options.getEdgesOutput(), mp);
            reportln("...OK!");
        }
        if (options.getEdgesMapOutput() != null) {
            report("Exporting edges index");
            new PathwayToEdgesIndexWriter(r).write(options.getEdgesMapOutput(), rm.getPathwaysToEdgesIndex());
            reportln("...OK!");
        }
    }
}
