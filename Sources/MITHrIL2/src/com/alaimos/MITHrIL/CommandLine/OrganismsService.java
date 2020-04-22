package com.alaimos.MITHrIL.CommandLine;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import dnl.utils.text.table.TextTable;

import java.util.HashMap;

/**
 * Lists all organisms supported by MITHrIL
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/01/2016
 */
public class OrganismsService implements Service {

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
        return "organisms";
    }

    @Override
    public String getDescription() {
        return "Lists all organisms and their characteristics";
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
        String[] columns = new String[]{"Id", "Name", "Has miRNA-targets interactions?", "Has TF-miRNAs activations?"};
        String[][] data =
                db.values().stream().map(s -> new String[]{s.getId(), s.getName(), ((s.hasMiRNA()) ? "Yes" : "No"),
                                                           ((s.hasTF()) ? "Yes" : "No")}).toArray(String[][]::new);
        TextTable tt = new TextTable(columns, data);
        tt.printTable();
    }
}
