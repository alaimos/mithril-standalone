package com.alaimos.Commons.CommandLine;

import org.kohsuke.args4j.Option;

/**
 * Default implementation of getHelp method for Options class
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/01/2016
 */
public abstract class AbstractOptions implements Options {

    @Option(name = "-h", aliases = {"--help"}, usage = "Print this message", help = true)
    protected boolean help = false;


    /**
     * Checks if the help has been requested
     *
     * @return help requested?
     */
    @Override
    public boolean getHelp() {
        return help;
    }

}
