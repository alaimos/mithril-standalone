package com.alaimos.MITHrIL;

import com.alaimos.BioClasses.Pathways.Node;
import com.alaimos.BioClasses.Pathways.Pathway;
import com.alaimos.BioClasses.Pathways.Repository;
import com.alaimos.Utils.Action;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EndpointExtractor {

    public static void main(String[] args) throws Exception {
        Repository r = Repository.getInstance();
        HashMap<String, HashSet<String>> pathwayEndpoints = new HashMap<String, HashSet<String>>();
        for (Pathway p : r) {
            final HashSet<String> tmpEndpoints = new HashSet<String>();
            final HashSet<String> visited = new HashSet<String>();
            for (Node n : p.getNodes()) {
                p.runDownstream(n, new Action<Node>() {
                    @Override
                    public ActionResult run(Node o) {
                        if (visited.add(o.getEntryId())) {
                            if (o.children().size() == 0) {
                                tmpEndpoints.add(o.getEntryId());
                                return ActionResult.PRUNE;
                            } else {
                                return ActionResult.CONTINUE;
                            }
                        }
                        return ActionResult.PRUNE;
                    }
                });
            }
            pathwayEndpoints.put(p.getId(), tmpEndpoints);
        }
        BufferedWriter w = new BufferedWriter(new FileWriter(args[0]));
        for (Map.Entry<String, HashSet<String>> e : pathwayEndpoints.entrySet()) {
            w.write("#");
            w.write(e.getKey());
            w.newLine();
            for (String n : e.getValue()) {
                w.write(n);
                w.newLine();
            }
        }
        w.close();
    }
}
