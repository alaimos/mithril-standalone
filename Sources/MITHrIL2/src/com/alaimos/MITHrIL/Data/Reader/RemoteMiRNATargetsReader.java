package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.MITHrIL.Data.Records.MiRNA;
import com.alaimos.MITHrIL.Data.Records.MiRNAsContainer;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.1
 * @since 12/12/2015
 */
public class RemoteMiRNATargetsReader extends RemoteTextFileReader {

    public RemoteMiRNATargetsReader(String url) {
        setUrl(url);
        init();
    }

    private void init() {
        setPersisted(true).setPersisted(true);
        setSeparator("\t").setFieldCountLimit(8);
    }

    public MiRNAsContainer readMiRNAs() {
        setFile("mirna-targets-" + FilenameUtils.getName(this.url));
        init();
        var data = read();
        MiRNAsContainer miRNAs = new MiRNAsContainer();
        for (var d : data) {
            var m = MiRNA.fromSplitString(d);
            if (!miRNAs.add(m)) {
                miRNAs.get(m.getMiRNAId()).addTarget(d);
            }
        }
        return miRNAs;
    }

}
