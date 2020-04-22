package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.MITHrIL.Data.Records.MiRNA;
import com.alaimos.MITHrIL.Data.Records.MiRNAsContainer;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class RemoteMiRNATFReader extends RemoteTextFileReader {

    public RemoteMiRNATFReader(String url) {
        setUrl(url);
        init();
    }

    private void init() {
        setPersisted(true).setPersisted(true);
        setSeparator("\t").setFieldCountLimit(5);
    }

    public HashMap<String, MiRNA> readTranscriptionFactors(MiRNAsContainer db) {
        setFile("mirna-tfs-" + FilenameUtils.getName(this.url));
        init();
        var data = read();
        for (var d : data) {
            if (!d[1].isEmpty()) {
                if (db.containsKey(d[1])) {
                    db.get(d[1]).addTranscriptionFactor(d);
                } else {
                    System.err.println(d[1] + " not found!!");
                }
            }
        }
        return db;
    }

}
