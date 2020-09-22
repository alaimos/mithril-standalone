package com.alaimos.PHENSIM.CommandLine;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.PHENSIM.PathwayEnricher.PathwayEnricherFactory;
import dnl.utils.text.table.TextTable;

/**
 * Lists all organisms supported by MITHrIL
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class EnrichersService implements Service {

    Options o = new AbstractOptions() {

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
        return "enrichers";
    }

    @Override
    public String getDescription() {
        return "Lists all available enrichers and their characteristics";
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
        String[] columns = new String[]{"Name", "Description"};
        String[][] data = PathwayEnricherFactory.getInstance().getAllEnrichers();
        TextTable tt = new TextTable(columns, data);
        tt.printTable();
    }
}
