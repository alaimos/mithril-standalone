package com.alaimos.other;

import com.alaimos.BioClasses.Cell.Gene;
import com.alaimos.BioClasses.Cell.GenesRepository;
import com.alaimos.BioClasses.Pathways.Node;
import com.alaimos.BioClasses.Pathways.Pathway;
import com.alaimos.BioClasses.Pathways.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GeneExport {

    //region Utilities

    static class ImportedData {
        public HashSet<String> arrayGenes = new HashSet<String>();
        public HashMap<String, Double> deltaE = new HashMap<String, Double>();
        public HashSet<String> rule = new HashSet<String>();
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
        result.put("output", null);
        result.put("pathways", null);
        result.put("levels", 0);
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
            } else if (args[i].equalsIgnoreCase("-out")) {
                if ((i + 1) < args.length) {
                    result.put("output", args[i + 1]);
                    i++;
                }
            } else if (args[i].equalsIgnoreCase("-levels")) {
                if ((i + 1) < args.length) {
                    result.put("levels", Integer.parseInt(args[i + 1]));
                    i++;
                }
            } else if (args[i].equalsIgnoreCase("-pathways")) {
                if ((i + 1) < args.length) {
                    Repository r = Repository.getInstance();
                    List<String> tmpList = new ArrayList<String>();
                    String[] tmp = args[i + 1].trim().split(",");
                    for (String aTmp : tmp) {
                        if (r.hasPathway(aTmp)) {
                            tmpList.add(aTmp);
                        } else if (Repository.categories.containsKey(aTmp)) {
                            tmpList.addAll(Repository.categories.get(aTmp));
                        }
                    }
                    tmp = new String[tmpList.size()];
                    tmp = tmpList.toArray(tmp);
                    if (tmp.length > 0) {
                        result.put("pathways", tmp);
                        i++;
                    }
                }
            }
        }
        return result;
    }

    //endregion

    //region Export Data

    public static void printData(String file, String pathway, List<String> data) {
        if (file != null) {
            try {
                BufferedWriter w = new BufferedWriter(new FileWriter(file));
                for (String s : data) {
                    w.write(s);
                    w.newLine();
                }
                w.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } else {
            System.out.println("------------------------------------------------------------------------------------------------------------");
            System.out.println(pathway);
            System.out.println("------------------------------------------------------------------------------------------------------------");
            for (String s : data) {
                System.out.println(s);
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
        }
    }

    public static void recursiveBuildList(Pathway p, List<Node> g, List<String> l, int lvl, ImportedData d) {
        if (lvl > 0) {
            GenesRepository gr = GenesRepository.getInstance();
            Gene gg;
            for (Node n : g) {
                if (n.getType() == Pathway.NodeType.MICRORNA) {
                    gg = gr.getGene(GenesRepository.mapMirBaseIdToKeggId(n.getEntryId()));
                } else {
                    gg = gr.getGene(n.getEntryId());
                }
                if (gg != null) {
                    l.add("" + gg.getKeggId() + ";" + gg.getName() + ";" + n.getType().toString() + ";" + gg.getMainSymbol());
                }/* else {
                    l.add("" + n.getEntryId() + ";" + n.getName() + ";" + n.getType().toString() + ";");
                }*/
                List<Node> t = p.getChildren(n);
                t.addAll(p.getParents(n));
                recursiveBuildList(p, t, l, lvl - 1, d);
            }
        }
    }

    public static void recursiveBuildList(Pathway p, List<String> l, int lvl, ImportedData d) {
        if (lvl > 0) {
            List<Node> ns = new ArrayList<Node>();
            List<Node> lst = p.getNodes();
            for (Node n : lst) {
                if (d.deltaE.containsKey(n.getEntryId())) {
                    ns.add(n);
                }
            }
            recursiveBuildList(p, ns, l, lvl, d);
        }
    }

    public static void runExport(ImportedData d, Repository r, int levels, String outDir, String[] pathways) throws Exception {
        String path = null;
        if (outDir != null && !outDir.equals("")) {
            File f = new File(outDir);
            if (!f.isDirectory()) {
                System.out.println("You need to specify a valid output directory.");
                System.exit(2);
            }
            path = f.getAbsolutePath() + File.separator;
        }
        if (levels <= 0) {
            GenesRepository gr = GenesRepository.getInstance();
            Gene g;
            for (String aPathway : pathways) {
                if (!r.hasPathway(aPathway)) continue;
                Pathway p = r.getPathway(aPathway);
                List<Node> l = p.getNodes();
                List<String> data = new ArrayList<String>(l.size());
                String file = (path != null) ? path + aPathway.replace(":", "_") + ".txt" : null;
                for (Node aNode : l) {
                    if (aNode.getType() == Pathway.NodeType.MICRORNA) {
                        g = gr.getGene(GenesRepository.mapMirBaseIdToKeggId(aNode.getEntryId()));
                    } else {
                        g = gr.getGene(aNode.getEntryId());
                    }
                    if (g != null) {
                        data.add("" + g.getKeggId() + ";" + g.getName() + ";" + aNode.getType().toString() + ";" + g.getMainSymbol());
                    }/* else {
                        data.add("" + aNode.getEntryId() + ";" + aNode.getName() + ";" + aNode.getType().toString() + ";");
                    }*/
                }
                printData(file, aPathway, data);
            }
        } else {
            for (String aPathway : pathways) {
                if (!r.hasPathway(aPathway)) continue;
                Pathway p = r.getPathway(aPathway);
                List<String> data = new ArrayList<String>();
                String file = (path != null) ? path + aPathway.replace(":", "_") + ".txt" : null;
                recursiveBuildList(p, data, levels, d);
                printData(file, aPathway, data);
            }
        }
    }

    //endregion

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        HashMap<String, Object> params = parseArgs(args);
        if ((params.get("file") == null ||
                (!params.get("method").equals("base") && !params.get("method").equals("enriched")) ||
                params.get("pathways") == null)) {
            System.out.println("Usage: \n    java -cp ImpactFactor.jar GeneExport -in [inputFile] -pathways listOfPathways [-out outputDirectory]");
            System.out.println(
                    "    java -cp ImpactFactor.jar GeneExport -in [inputFile] -pathways listOfPathways -method base [-out outputDirectory]");
            System.out.println(
                    "    java -cp ImpactFactor.jar GeneExport -in [inputFile] -pathways listOfPathways -method enriched [-out outputDirectory]");
            System.out.println("\nParameters:");
            System.out.println("    -in inputFile               (Required) Specifies the input file;");
            System.out.println("    -method method              (Required) Specifies the method of calculation: \"base\" uses standard KEGG pathways, \"enriched\" uses KEGG pathway with miRNAs;");
            System.out.println("    -pathways listOfPathways    (Required) A list of pathways or categories separated by commas;");
            System.out.println("    -out outputDirectory        (Optional) Writes the output to structured files whose fields are separated by semicolons;");
            System.out.println("    -levels numberOfLevels      (Optional) The number of levels to iterate the process of gene selection. 0 = disabled.");
            System.out.println("\nInput File Format:");
            System.out.println("    The input file consists of three sections, each separated by a header.\n" +
                    "    The headings for each section are, respectively: \"#-G\", \"#-D\", \"#-R\".\n" +
                    "    The format of each section, and their meaning is as follows:\n" +
                    "    \tSection 1) a list of gene identifiers (one per line) in KEGG format (for example hsa:10).\n" +
                    "    \tSection 2) a list of differentially expressed gene identifiers (one per line) and the \n" +
                    "    \t           magnitude of the difference calculated by Log-Fold-Change (separating the two \n" +
                    "    \t           fields by a semicolon).\n" +
                    "    \tSection 3) a list of gene identifiers (one per line) on which restrict the computation. \n" +
                    "    \t           The list can be empty, but the header is mandatory.");
        } else {
            File f = new File((String) params.get("file"));
            if (!f.exists()) {
                System.out.println("You need to specify a valid input file!");
                System.exit(1);
            }
            ImportedData imp = importData((String) params.get("file"));
            int levels = (Integer) params.get("levels");
            Repository r = (params.get("method").equals("enriched")) ? Repository.getEnrichedInstance() : Repository.getInstance();
            runExport(imp, r, levels, (String) params.get("output"), (String[]) params.get("pathways"));
        }
    }

}