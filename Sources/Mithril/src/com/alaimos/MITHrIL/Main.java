package com.alaimos.MITHrIL;

import com.alaimos.BioClasses.Pathways.Pathway;
import com.alaimos.BioClasses.Pathways.Repository;

import java.io.*;
import java.util.*;

public class Main {

    //region Utilities

    static class ImportedData {
        public HashSet<String>         arrayGenes = new HashSet<String>();
        public HashMap<String, Double> deltaE     = new HashMap<String, Double>();
        public HashSet<String>         rule       = new HashSet<String>();
    }

    static class Result implements Comparable<Result> {

        public static       boolean writingOutputFile = false;
        public static final boolean ORDER_P_VALUES    = true;

        private Pathway pathway         = null;
        private double  impactFactor    = 0.0d;
        private double  pValue          = 1.0d;
        private double  accumulation    = 0.0d;
        private double  rawAccumulation = 0.0d;
        private double  totalPerturb    = 0.0d;
        private double  prob            = 0.0d;

        public Result(Pathway pathway, double impactFactor, double[] pValue, double acc, double tp, double p) {
            this.pathway = pathway;
            this.impactFactor = impactFactor;
            this.pValue = pValue[0];
            this.accumulation = acc - pValue[1];
            this.rawAccumulation = acc;
            this.totalPerturb = tp;
            this.prob = p;
        }

        public Pathway getPathway() {
            return pathway;
        }

        public double getImpactFactor() {
            return impactFactor;
        }

        public double getPValue() {
            return pValue;
        }

        @Override
        public int compareTo(Result o) {
            if (ORDER_P_VALUES) {
                return Double.compare(pValue, o.pValue);
            } else {
                return Double.compare(impactFactor, o.impactFactor);
            }
        }

        @Override
        public String toString() {
            if (writingOutputFile) {
                return pathway.getId() + ";" + pathway.getName() + ";" + impactFactor + ";" + accumulation + ";" +
                        pValue + ";" + rawAccumulation + ";" + totalPerturb + ";" + prob;
            } else {
                return pathway.getName() + " (" + pathway.getId() + ")\t" + impactFactor + "\t" + accumulation + "\t" +
                        pValue + "\t" + rawAccumulation + "\t" + totalPerturb + "\t" + prob;
            }
        }
    }

    public static ImportedData importData(String file) {
        ImportedData imp = new ImportedData();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            boolean readingGenes = false,
                    readingDiff = false,
                    readingRule = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("#-g")) {
                    readingGenes = true;
                    readingDiff = readingRule = false;
                    continue;
                } else if (line.equalsIgnoreCase("#-d")) {
                    readingDiff = true;
                    readingGenes = readingRule = false;
                    continue;
                } else if (line.equalsIgnoreCase("#-r")) {
                    readingRule = true;
                    readingGenes = readingDiff = false;
                    continue;
                }
                if (readingGenes) {
                    imp.arrayGenes.add(line.toLowerCase());
                } else if (readingDiff) {
                    String[] dt = line.split(";");
                    if (dt.length >= 2) {
                        imp.deltaE.put(dt[0].toLowerCase(), Double.parseDouble(dt[1]));
                    }
                } else if (readingRule) {
                    imp.rule.add(line.toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return imp;
    }

    public static HashMap<String, Object> parseArgs(String[] args) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("file", null);
        result.put("method", "base");
        result.put("rule", false);
        result.put("output", null);
        result.put("filter", null);
        result.put("example", false);
        result.put("decoy", false);
        result.put("endpoints", false);
        result.put("pv", 2001);
        result.put("endpointsOut", null);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-in")) {
                if ((i + 1) < args.length) {
                    result.put("file", args[i + 1]);
                    i++;
                }
            } else if (args[i].equalsIgnoreCase("-method")) {
                if ((i + 1) < args.length) {
                    result.put("method", args[i + 1].toLowerCase());
                    i++;
                }
            } else if (args[i].equalsIgnoreCase("-endpoints")) {
                if ((i + 1) < args.length) {
                    result.put("endpoints", true);
                    result.put("endpointsOut", args[i + 1]);
                    i++;
                }
            } else if (args[i].equalsIgnoreCase("-userule")) {
                result.put("rule", true);
            } else if (args[i].equalsIgnoreCase("-example")) {
                result.put("example", true);
            } else if (args[i].equalsIgnoreCase("-decoy")) {
                result.put("decoy", true);
            } else if (args[i].equalsIgnoreCase("-out")) {
                if ((i + 1) < args.length) {
                    result.put("output", args[i + 1]);
                    i++;
                }
            } else if (args[i].equalsIgnoreCase("-filter")) {
                if ((i + 1) < args.length) {
                    String[] tmp = args[i + 1].replace(" ", "").split(",");
                    if (tmp.length > 0) {
                        result.put("filter", tmp);
                        i++;
                    }
                }
            } else if (args[i].equalsIgnoreCase("-pv")) {
                if ((i + 1) < args.length) {
                    result.put("pv", Integer.parseInt(args[i + 1]));
                }
            }
        }
        return result;
    }

    public static Result[] combineResults(Repository r, double[] ifs, double[][] pvs, double[] acc, double[] tp,
                                          double[] pi) {
        Result[] res = new Result[r.size()];
        for (int i = 0; i < r.size(); i++) {
            res[i] = new Result(r.get(i), ifs[i], pvs[i], acc[i], tp[i], pi[i]);
        }
        return res;
    }

    public static HashMap<String, ArrayList<String>> readEndpoints() throws IOException {
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Main.class.getClass().getResourceAsStream("/com/alaimos/BioResources/endpoints.txt")));
        String currPathway = null;
        ArrayList<String> endpoints = null;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                if (currPathway != null && endpoints != null) {
                    result.put(currPathway, endpoints);
                }
                currPathway = line.replace("#", "");
                endpoints = new ArrayList<String>();
            } else {
                if (endpoints != null) endpoints.add(line);
            }
        }
        if (currPathway != null && endpoints != null) {
            result.put(currPathway, endpoints);
        }
        return result;
    }

    //endregion

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        HashMap<String, Object> params = parseArgs(args);
        if ((params.get("file") == null ||
                (!params.get("method").equals("base") && !params.get("method").equals("enriched"))) &&
                !(Boolean) params.get("example")) {
            System.out.println(
                    "Usage: \n    java -jar ImpactFactor.jar -in [inputFile] [-useRule] [-out outputFile] [-filter " +
                            "listOfCategories] [-decoy] [-endpoints outFile]");
            System.out.println(
                    "    java -jar ImpactFactor.jar -in [inputFile] -method base [-useRule] [-out outputFile] " +
                            "[-filter listOfCategories] [-decoy] [-endpoints outFile]");
            System.out.println(
                    "    java -jar ImpactFactor.jar -in [inputFile] -method enriched [-useRule] [-out outputFile] " +
                            "[-filter listOfCategories] [-decoy] [-endpoints outFile]");
            System.out.println("\nParameters:");
            System.out.println("    -in inputFile               (Required) Specifies the input file;");
            System.out.println(
                    "    -method method              (Required) Specifies the method of calculation: \"base\" uses " +
                            "standard KEGG pathways, \"enriched\" uses KEGG pathway with miRNAs;");
            System.out.println(
                    "    -useRule                    (Optional) If used, specifies that the method will be applied " +
                            "only to a small group of genes indicated in the input file;");
            System.out.println(
                    "    -out outputFile             (Optional) Writes the output to a structured file whose fields " +
                            "are separated by semicolons;");
            System.out.println(
                    "    -filter listOfCategories    (Optional) Performs calculations only on a select group of " +
                            "pathways. Each element of the list is separated by commas.");
            System.out.println(
                    "                                           The list may contain one or more of the following " +
                            "values: \"diseases\", \"processes\", \"immunesystem\", ");
            System.out.println(
                    "                                           \"nervoussystem\", or \"enviromentadaptation\";");
            System.out.println(
                    "    -decoy                      (Optional) Add decoy pathways to test the accuracy of this " +
                            "method;");
            System.out.println("    -endpoints outFile          (Optional) Save endpoints perturbations.");
            System.out.println("    -example                    (Optional) Shows an example of input file.");
            System.out.println("\nInput File Format:");
            System.out.println("    The input file consists of three sections, each separated by a header.\n" +
                    "    The headings for each section are, respectively: \"#-G\", \"#-D\", \"#-R\".\n" +
                    "    The format of each section, and their meaning is as follows:\n" +
                    "    \tSection 1) a list of gene identifiers (one per line) in KEGG format (for example hsa:10)" +
                    ".\n" +
                    "    \tSection 2) a list of differentially expressed gene identifiers (one per line) and the \n" +
                    "    \t           magnitude of the difference calculated by Log-Fold-Change (separating the two " +
                    "\n" +
                    "    \t           fields by a semicolon).\n" +
                    "    \tSection 3) a list of gene identifiers (one per line) on which restrict the computation. \n" +
                    "    \t           The list can be empty, but the header is mandatory.");
        } else if ((Boolean) params.get("example")) {
            try {
                Main m = new Main();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                m.getClass().getResourceAsStream("/com/alaimos/BioResources/example.txt")));
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } else {
            File f = new File((String) params.get("file"));
            if (!f.exists()) {
                System.out.println("You need to specify a valid file!!");
                System.exit(1);
            }
            ImportedData imp = importData((String) params.get("file"));
            AbstractCalculator calc;
            if (params.get("filter") != null) {
                Repository.getInstance().filter((String[]) params.get("filter"));
            }
            Repository r;
            if (params.get("method").equals("enriched")) {
                r = Repository.getEnrichedInstance();
                calc = new MiRNACalculator();
            } else {
                r = Repository.getInstance();
                calc = new BaseCalculator();
            }
            if ((Boolean) params.get("decoy")) {
                r.addDecoys();
            }
            calc.setArrayGenes(imp.arrayGenes).setDeltaE(imp.deltaE).setPV((int) params.get("pv"));
            if ((Boolean) params.get("rule")) {
                String[] rule = new String[imp.rule.size()];
                imp.rule.toArray(rule);
                calc.setRule(rule);
            }
            double[] impactFactor = calc.computeImpactFactor(),
                    accumulation = calc.computeAccumulation(),
                    totalPerturbation = calc.computeTotalPerturbation(),
                    probs = calc.computeProbability();
            double[][] pValues = calc.computePValue(r, impactFactor);
            Result[] results = combineResults(r, impactFactor, pValues, accumulation, totalPerturbation, probs);
            if (Result.ORDER_P_VALUES) {
                Arrays.sort(results);
            } else {
                Arrays.sort(results, Collections.reverseOrder());
            }
            Boolean ep = (Boolean) params.get("endpoints");
            String epOut = (String) params.get("endpointsOut");
            String output = (String) params.get("output");

            if (output != null && !output.isEmpty()) {
                Result.writingOutputFile = true;
                BufferedWriter bw = new BufferedWriter(new FileWriter(output, false));
                bw.write(
                        "Pathway ID;Pathway Name;Impact Factor;Perturbation Accumulation;P-Value;Raw Accumulation;" +
                                "Total Perturbation;Probability");
                bw.newLine();
                for (Result res : results) {
                    bw.write(res.toString());
                    bw.newLine();
                }
                bw.close();
            } else {
                System.out.println(
                        "Pathway Name (KEGG ID)\tImpact Factor\tPerturbation Accumulation\tP-Value\tRaw " +
                                "Accumulation\tTotal Perturbation\tProbability");
                for (Result res : results) {
                    System.out.println(res);
                }
            }
            if (ep && epOut != null && !epOut.isEmpty()) {
                HashMap<String, ArrayList<String>> endpoints = readEndpoints();
                BufferedWriter bw = new BufferedWriter(new FileWriter(epOut, false));
                Pathway p;
                double pert;
                bw.write("Pathway ID;Endpoint ID; Perturbation");
                bw.newLine();
                for (Map.Entry<String, ArrayList<String>> e : endpoints.entrySet()) {
                    p = r.getPathway(e.getKey());
                    for (String endpoint : e.getValue()) {
                        pert = calc.perturbationFactor(p, p.getNode(endpoint));
                        bw.write(e.getKey() + ";" + endpoint + ";" + Double.toString(pert));
                        bw.newLine();
                    }
                }
                bw.close();
            }
        }
    }
}
