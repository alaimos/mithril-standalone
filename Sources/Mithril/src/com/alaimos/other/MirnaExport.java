package com.alaimos.other;

import com.alaimos.BioClasses.Pathways.Edge;
import com.alaimos.BioClasses.Pathways.Node;
import com.alaimos.BioClasses.Pathways.Pathway;
import com.alaimos.BioClasses.Pathways.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

public class MirnaExport {

    //region Utilities

    public static HashMap<String, Object> parseArgs(String[] args) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("output", null);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-out")) {
                if ((i + 1) < args.length) {
                    result.put("output", args[i + 1]);
                    i++;
                }
            }
        }
        return result;
    }

    //endregion

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        HashMap<String, Object> params = parseArgs(args);
        if (params.get("output") == null) {
            System.out.println("Usage: \n    java -cp ImpactFactor.jar MirnaExport -out [outputFile]");
            System.out.println("\nParameters:");
            System.out.println("    -out outputFile       The output file.");
        } else {
            BufferedWriter bw = new BufferedWriter(new FileWriter((String) params.get("output")));
            Repository r = Repository.getEnrichedInstance();
            for (Pathway p : r) {
                String pid = p.getId();
                String pn = p.getName();
                for (Node n : p.getNodes()) {
                    if (n.getType() == Pathway.NodeType.MICRORNA) {
                        String nid = n.getEntryId();
                        for (Node m : p.getChildren(n)) {
                            Edge e = p.getEdge(n, m);
                            if (e.getType() == Pathway.EdgeType.MGRel) {
                                String s = "" + nid + ";" + m.getEntryId() + ";" + pid + ";" + pn;
                                bw.write(s);
                                bw.newLine();
                            }
                        }
                    }
                }
            }
            bw.close();
        }
    }

}