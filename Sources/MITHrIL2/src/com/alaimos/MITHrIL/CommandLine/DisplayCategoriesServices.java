package com.alaimos.MITHrIL.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.MITHrIL.CommandLine.Options.DisplayCategoriesOptions;
import com.alaimos.MITHrIL.Common;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import dnl.utils.text.table.TextTable;

import java.util.HashMap;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class DisplayCategoriesServices implements Service {

    protected DisplayCategoriesOptions options = new DisplayCategoriesOptions();

    @Override
    public String getShortName() {
        return "pathway-categories";
    }

    @Override
    public String getDescription() {
        return "shows all available pathway categories for a species";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    /**
     * Read pathway repository
     *
     * @param s species definition
     * @return the repository
     */
    protected RepositoryInterface getPathwayRepository(Species s) {
        return Common.getPathwayRepository(s, true, EvidenceType.UNKNOWN, false, null, System.err::print);
    }

    @Override
    public void run() {
        System.err.print("Reading species database");
        RemoteSpeciesDatabaseReader speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
        HashMap<String, Species> db = speciesDbReader.readSpecies();
        System.err.println("...OK!");
        if (!db.containsKey(options.getOrganism())) {
            throw new InputParametersException("Invalid species: species not found.");
        }
        Species s = db.get(options.getOrganism());
        System.err.print("Reading pathways for " + s.getName());
        RepositoryInterface r = getPathwayRepository(s);
        System.err.println("...OK!");
        List<String> categories = r.getCategories();
        String[] columns = new String[]{"Category"};
        String[][] data = categories.stream().map(c -> new String[]{c}).toArray(String[][]::new);
        TextTable tt = new TextTable(columns, data);
        tt.printTable();
    }
}
