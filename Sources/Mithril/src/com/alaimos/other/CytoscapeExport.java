package com.alaimos.other;

import com.alaimos.BioClasses.Cell.Gene;
import com.alaimos.BioClasses.Cell.GenesRepository;
import com.alaimos.BioClasses.Cell.MicroRNA;
import com.alaimos.BioClasses.Pathways.Edge;
import com.alaimos.BioClasses.Pathways.Node;
import com.alaimos.BioClasses.Pathways.Pathway;
import com.alaimos.BioClasses.Pathways.Repository;
import com.alaimos.MITHrIL.AbstractCalculator;
import com.alaimos.MITHrIL.BaseCalculator;
import com.alaimos.MITHrIL.MiRNACalculator;

import java.io.*;
import java.util.*;

public class CytoscapeExport {

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
        result.put("rule", false);
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
            } else if (args[i].equalsIgnoreCase("-userule")) {
                result.put("rule", true);
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

    public static void runExport(ImportedData imp, Repository r, GenesRepository gr, AbstractCalculator calc,
                                 String[] pathways, File fOut) throws Exception {
        String path = fOut.getAbsolutePath() + File.separator, sifFile, nodesTable;
        List<Edge> l;
        List<Node> nl;
        Pathway p;
        BufferedWriter bw;
        Node n1, n2;
        Gene g1, g2;
        String[] asTmp;
        for (String aPathway : pathways) {
            sifFile = path + aPathway.replace(':', '_') + ".sif";
            nodesTable = path + aPathway.replace(':', '_') + ".nodes.txt";
            bw = new BufferedWriter(new FileWriter(sifFile));
            p = r.getPathway(aPathway);
            l = p.getEdges();
            for (Edge e : l) {
                n1 = e.getStart();
                n2 = e.getEnd();
                g1 = (n1.getType() == Pathway.NodeType.MICRORNA) ?
                        gr.getGene(GenesRepository.mapMirBaseIdToKeggId(n1.getEntryId())) :
                        gr.getGene(n1.getEntryId());
                g2 = (n2.getType() == Pathway.NodeType.MICRORNA) ?
                        gr.getGene(GenesRepository.mapMirBaseIdToKeggId(n2.getEntryId())) :
                        gr.getGene(n2.getEntryId());
                if (g1 != null && g2 != null) {
                    bw.write(g1.getMainSymbol() + " " + e.getSubType().toString() + " " + g2.getMainSymbol());
                    bw.newLine();
                }
            }
            bw.close();
            bw = new BufferedWriter(new FileWriter(nodesTable));
            bw.write("Gene Id\tGene Name\tKegg Id\tMirBase Id\tEntrez Id\tKegg Link\tNCBI Link\tMirBase Link\t");
            bw.write("Synonyms\tPerturbation Factor");
            bw.newLine();
            nl = p.getNodes();
            for (Node n : nl) {
                g1 = (n.getType() == Pathway.NodeType.MICRORNA) ?
                        gr.getGene(GenesRepository.mapMirBaseIdToKeggId(n.getEntryId())) :
                        gr.getGene(n.getEntryId());
                if (g1 != null) {
                    bw.write(g1.getMainSymbol() + "\t" + g1.getName() + "\t" + g1.getKeggId() + "\t");
                    bw.write(((n.getType() == Pathway.NodeType.MICRORNA) ? ((MicroRNA) g1).getMirBaseId() : "-") + "\t");
                    bw.write(g1.getEntrezId() + "\t" + g1.getKeggLink() + "\t" + g1.getNCBILink() + "\t");
                    bw.write(((n.getType() == Pathway.NodeType.MICRORNA) ? ((MicroRNA) g1).getMirBaseLink() : "-") + "\t");
                    asTmp = g1.getOtherSymbols();
                    if (asTmp == null || asTmp.length <= 0) {
                        bw.write("-\t");
                    } else {
                        bw.write(Arrays.toString(g1.getOtherSymbols()).replace("[", "").replace("]", "") + "\t");
                    }
                    bw.write(Double.toString(calc.perturbationFactor(p, n)));
                    bw.newLine();
                }
            }
            bw.close();
        }
        /*if (outDir != null && !outDir.equals("")) {
            File f = new File(outDir);
            if (!f.isDirectory()) {
                throw new Exception("The output directory must be a directory.");
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
                /*}
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
        }*/
    }

    //endregion

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        HashMap<String, Object> params = parseArgs(args);
        if ((params.get("file") == null ||
                (!params.get("method").equals("base") && !params.get("method").equals("enriched")) ||
                params.get("pathways") == null || params.get("output") == null)) {
            System.out.println("Usage: \n    java -cp ImpactFactor.jar CytoscapeExport -in [inputFile] -pathways listOfPathways [-out outputDirectory]");
            System.out.println(
                    "    java -cp ImpactFactor.jar CytoscapeExport -in [inputFile] -pathways listOfPathways -method base [-out outputDirectory]");
            System.out.println(
                    "    java -cp ImpactFactor.jar CytoscapeExport -in [inputFile] -pathways listOfPathways -method enriched [-out outputDirectory]");
            System.out.println("\nParameters:");
            System.out.println("    -in inputFile               (Required) Specifies the input file;");
            System.out.println("    -method method              (Required) Specifies the method of calculation: \"base\" uses standard KEGG pathways, \"enriched\" uses KEGG pathway with miRNAs;");
            System.out.println("    -pathways listOfPathways    (Required) A list of pathways or categories separated by commas;");
            System.out.println("    -out outputDirectory        (Required) The directory where the output will be written;");
            System.out.println("    -useRule                    (Optional) If used, specifies that the method will be applied only to a small group of genes indicated in the input file;");
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
            File fIn = new File((String) params.get("file"));
            if (!fIn.exists()) {
                System.out.println("You need to specify a valid input file!!");
                System.exit(1);
            }
            File fOut = new File((String) params.get("output"));
            if (!fOut.exists()) {
                fOut.mkdirs();
            }
            if (!fOut.isDirectory()) {
                System.out.println("You need to specify a valid output directory!!");
                System.exit(2);
            }
            ImportedData imp = importData(fIn.getAbsolutePath());
            boolean isEnriched = ((String) params.get("method")).equalsIgnoreCase("enriched");
            final GenesRepository gr = GenesRepository.getInstance();
            final Repository r = isEnriched ? Repository.getEnrichedInstance() : Repository.getInstance();
            final AbstractCalculator calc = isEnriched ? new MiRNACalculator() : new BaseCalculator();
            String[] pathways = (String[]) params.get("pathways");
            r.filter(pathways);
            calc.setArrayGenes(imp.arrayGenes).setDeltaE(imp.deltaE);
            if ((Boolean) params.get("rule")) {
                calc.setRule(imp.rule.toArray(new String[imp.rule.size()]));
            }
            //Precomputes the impact factor and the accumulation to cache intermediate values​​.
            calc.computeImpactFactor();
            calc.computeAccumulation();
            runExport(imp, r, gr, calc, pathways, fOut);
        }
    }

}