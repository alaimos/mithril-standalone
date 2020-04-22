package com.alaimos.SPECifIC.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.Commons.Utils.Utils;
import com.alaimos.SPECifIC.CommandLine.Options.BuildIndexOptions;
import com.alaimos.MITHrIL.Common;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Writer.BinaryWriter;

import java.io.File;
import java.util.HashMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class BuildIndexService implements Service {

    protected BuildIndexOptions options = new BuildIndexOptions();

    @Override
    public String getShortName() {
        return "index";
    }

    @Override
    public String getDescription() {
        return "build a binary index of the pathway repository.";
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
                null, this::report, false);
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
        String baseName =
                "index-" + s.getId() + "-" + options.getEnrichmentEvidenceType() + "-";
        report("Writing repository index");
        new BinaryWriter<RepositoryInterface>().write(new File(Utils.getAppDir(), baseName + "repository.datz"), r);
        reportln("...OK!");
    }
}
