package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.MITHrIL.Constants;
import com.alaimos.MITHrIL.Data.Records.Species;

import java.util.HashMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class RemoteSpeciesDatabaseReader extends RemoteTextFileReader {


    private static RemoteSpeciesDatabaseReader instance = new RemoteSpeciesDatabaseReader();

    public static RemoteSpeciesDatabaseReader getInstance() {
        return instance;
    }

    private RemoteSpeciesDatabaseReader() {
        init();
    }

    private void init() {
        setPersisted(true).setUrl(Constants.SPECIES_INDEX_URL).setPersisted(true).setFile(Constants.SPECIES_INDEX_FILE);
        setSeparator("\t").setFieldCountLimit(7);
    }

    public HashMap<String, Species> readSpecies() {
        init();
        var species = read();
        HashMap<String, Species> speciesMap = new HashMap<>();
        for (var s : species) {
            var ss = Species.fromSplitString(s);
            speciesMap.put(ss.getId(), ss);
        }
        return speciesMap;
    }

}
