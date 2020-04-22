package com.alaimos.MITHrIL.CommandLine.Options;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import org.kohsuke.args4j.Option;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class DisplayCategoriesOptions extends AbstractOptions {

    @Option(name = "-o", aliases = "-organism", usage = "the organism used for analysis.\nA list of organisms " +
            "can be obtained by launching the specific utility made available by this software.")
    protected String organism = "hsa";

    public String getOrganism() {
        return organism;
    }
}