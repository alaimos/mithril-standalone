package com.alaimos.MITHrIL.CommandLine.Options;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.Arrays;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class MITHrILConversionOptions extends AbstractOptions {

    public enum ConversionType {
        INPUT_OLD_TO_NEW,
        OUTPUT_NEW_TO_OLD,
        ENDPOINTS_NEW_TO_OLD
    }

    @Option(name = "-t", aliases = "-type", usage = "Type of conversion.", required = true)
    protected ConversionType typeOfConversion = null;

    @Option(name = "-i", aliases = "-in", usage = "input files.", required = true, metaVar = "file1, file2, ...")
    protected void setInput(String s) {
        input = Arrays.stream(s.split(",")).map(String::trim).map(File::new).toArray(File[]::new);
    }

    protected File[] input = null;

    @Option(name = "-o", aliases = "-out", usage = "output files. The number of output files should be the same as " +
            "input one, except for INPUT_OLD_TO_NEW which allows the creation of a single batch output file from " +
            "multiple input file.", required = true, metaVar = "file1, file2, ...")
    protected void setOutput(String s) {
        output = Arrays.stream(s.split(",")).map(String::trim).map(File::new).toArray(File[]::new);
    }

    protected File[] output = null;

    public ConversionType getTypeOfConversion() {
        return typeOfConversion;
    }

    public File[] getInput() {
        return input;
    }

    public File[] getOutput() {
        return output;
    }
}