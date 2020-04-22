package com.alaimos.MITHrIL.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.CommandLine.Options.MITHrILConversionOptions;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alaimos.MITHrIL.CommandLine.Options.MITHrILConversionOptions.ConversionType.INPUT_OLD_TO_NEW;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class MITHrILConversionService implements Service {

    protected MITHrILConversionOptions options = new MITHrILConversionOptions();

    @Override
    public String getShortName() {
        return "convert";
    }

    @Override
    public String getDescription() {
        return "conversion between MITHrIL 2 and MITHrIL 1";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    private String newIdToOld(String id) {
        id = id.toLowerCase();
        if (id.matches("[0-9]+")) {
            id = "hsa:" + id;
        }
        return id;
    }

    private String oldIdToNew(String id) {
        if (id.startsWith("hsa:")) {
            id = id.replace("hsa:", "");
        } else if (id.startsWith("hsa-mir")) {
            id = id.replace("hsa-mir", "hsa-miR");
        }
        return id;
    }

    /**
     * Read old input format
     *
     * @param input input file
     * @return a map node=log-fold-change
     */
    @Nullable
    private Map<String, Double> readOldInputFile(File input) {
        Map<String, Double> expressions = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            boolean readingGenes = false, readingDiff = false;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("#-g")) {
                    readingGenes = true;
                    readingDiff = false;
                    continue;
                } else if (line.equalsIgnoreCase("#-d")) {
                    readingDiff = true;
                    readingGenes = false;
                    continue;
                }
                if (readingGenes) {
                    expressions.put(oldIdToNew(line), 0.0);
                } else if (readingDiff) {
                    String[] dt = line.split(";");
                    if (dt.length >= 2) {
                        expressions.put(oldIdToNew(dt[0]), Double.parseDouble(dt[1]));
                    }
                }
            }
            return expressions;
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * Converts old mithril input to use with this version
     */
    private void inputOldToNew(File input, File output) {
        Map<String, Double> expressions = readOldInputFile(input);
        if (expressions == null) throw new InputParametersException("Invalid input file: unknown input format.");
        try (PrintStream ps = new PrintStream(new FileOutputStream(output))) {
            expressions.forEach((s, v) -> ps.println(s + "\t" + v));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Converts old mithril inputs to use with this version
     */
    private void inputOldToNewBatch(List<File> input, File output) {
        Map<String, Map<String, Double>> expressions = new LinkedHashMap<>();
        for (File f : input) {
            Map<String, Double> expr = readOldInputFile(f);
            if (expr == null) continue;
            expressions.put(FilenameUtils.getBaseName(f.getName()), expr);
        }
        HashSet<String> allNodes = expressions.entrySet().stream().flatMap(e -> e.getValue().keySet().stream())
                                              .collect(Collectors.toCollection(HashSet::new));
        try (PrintStream ps = new PrintStream(new FileOutputStream(output))) {
            ps.println(expressions.keySet().stream().collect(Collectors.joining("\t")));
            allNodes.forEach(n -> {
                ps.print(n + "\t");
                ps.println(expressions.entrySet().stream().map(e -> e.getValue().get(n))
                                      .map(e -> Double.toString((e == null) ? 0.0 : e))
                                      .collect(Collectors.joining("\t")));
            });
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Converts new output format to old format
     */
    private void outputNewToOld(File input, File output) {
        try (PrintStream ps = new PrintStream(new FileOutputStream(output))) {
            ps.println("Pathway ID;Pathway Name;Impact Factor;Perturbation Accumulation;P-Value;Raw Accumulation;" +
                    "Total Perturbation;Probability");
            try (BufferedReader r = new BufferedReader(new FileReader(input))) {
                String line;
                String[] s;
                while ((line = r.readLine()) != null) {
                    if (!line.isEmpty()) {
                        if (line.startsWith("#")) continue;
                        s = line.split("\t", -1);
                        if (s.length == 9) {
                            ps.println(s[0] + ";" + s[1] + ";" + s[3] + ";" + s[6] + ";" + s[7] + ";" + s[2] + ";" +
                                    s[5] + ";" + s[4]);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts new endpoints format to new format
     */
    private void endpointsNewToOld(File input, File output) {
        try (PrintStream ps = new PrintStream(new FileOutputStream(output))) {
            ps.println("Pathway ID;Endpoint ID;Perturbation");
            try (BufferedReader r = new BufferedReader(new FileReader(input))) {
                String line;
                String[] s;
                while ((line = r.readLine()) != null) {
                    if (!line.isEmpty()) {
                        if (line.startsWith("#")) continue;
                        s = line.split("\t", -1);
                        if (s.length == 6) {
                            ps.println(s[0] + ";" + newIdToOld(s[2]) + ";" + s[4]);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        File[] input = options.getInput(), output = options.getOutput();
        List<File> validInput = Arrays.stream(input).filter(File::exists).collect(Collectors.toList());
        int s = validInput.size();
        MITHrILConversionOptions.ConversionType t = options.getTypeOfConversion();
        if (s == 0) {
            throw new InputParametersException("Invalid input files: at least one valid file should be specified");
        }
        if (t == INPUT_OLD_TO_NEW && output.length < s && output.length != 1) {
            throw new InputParametersException(
                    "Invalid output files: you must specify one output file for each input file or one single output " +
                            "file to build a batch input");
        }
        if (t != INPUT_OLD_TO_NEW && output.length < s) {
            throw new InputParametersException(
                    "Invalid output files: you must specify one output file for each input file");
        }
        if (t == INPUT_OLD_TO_NEW && output.length == 1 && s > 1) {
            inputOldToNewBatch(validInput, output[0]);
        } else {
            IntStream.range(0, s).mapToObj(i -> new Pair<>(validInput.get(i), output[i])).parallel().forEach(p -> {
                switch (t) {
                    case INPUT_OLD_TO_NEW:
                        inputOldToNew(p.getFirst(), p.getSecond());
                        break;
                    case OUTPUT_NEW_TO_OLD:
                        outputNewToOld(p.getFirst(), p.getSecond());
                        break;
                    case ENDPOINTS_NEW_TO_OLD:
                        endpointsNewToOld(p.getFirst(), p.getSecond());
                        break;
                    default:
                        throw new InputParametersException("Invalid type of conversion.");
                }

            });
        }
    }
}
