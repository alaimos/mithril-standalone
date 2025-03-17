package com.alaimos.PHENSIM.Data.Reader;

import com.alaimos.MITHrIL.Constants;
import com.alaimos.MITHrIL.Data.Reader.RemoteTextFileReader;
import com.alaimos.MITHrIL.Data.Records.Species;

import java.util.HashMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class RemoteReactomeIndexReader extends RemoteTextFileReader {


    private static RemoteReactomeIndexReader instance = new RemoteReactomeIndexReader();

    public static RemoteReactomeIndexReader getInstance() {
        return instance;
    }

    private RemoteReactomeIndexReader() {
        init();
    }

    private void init() {
        setPersisted(true).setUrl(Constants.REACTOME_INDEX_URL).setPersisted(true).setFile(Constants.REACTOME_INDEX_FILE);
        setSeparator("\t").setFieldCountLimit(2);
    }

    public HashMap<String, String> readIndex() {
        init();
        var species = read();
        HashMap<String, String> speciesMap = new HashMap<>();
        for (var s : species) {
            speciesMap.put(s[0], s[1]);
        }
        return speciesMap;
    }

}
