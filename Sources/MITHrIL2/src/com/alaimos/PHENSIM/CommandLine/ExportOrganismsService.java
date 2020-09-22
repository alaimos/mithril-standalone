package com.alaimos.PHENSIM.CommandLine;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Records.Species;

import java.util.HashMap;

/**
 * Export all organisms supported by PHENSIM
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class ExportOrganismsService implements Service {

    private Options o = new AbstractOptions() {

        /**
         * Checks if the help has been requested
         *
         * @return help requested?
         */
        @Override
        public boolean getHelp() {
            return super.getHelp();
        }
    };

    @Override
    public String getShortName() {
        return "exporg";
    }

    @Override
    public String getDescription() {
        return "Export all organisms supported by PHENSIM";
    }

    @Override
    public Options getOptions() {
        return o;
    }

    /**
     * Gets the list of all organisms and prints it
     */
    @Override
    public void run() {
        RemoteSpeciesDatabaseReader speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
        HashMap<String, Species> db = speciesDbReader.readSpecies();
        System.out.println("#Id\tName");
        for (Species s : db.values()) {
            System.out.println(s.getId() + "\t" + s.getName());
        }
    }
}
