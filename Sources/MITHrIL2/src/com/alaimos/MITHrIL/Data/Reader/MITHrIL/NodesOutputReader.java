package com.alaimos.MITHrIL.Data.Reader.MITHrIL;

import com.alaimos.Commons.Reader.AbstractDataReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 12/12/2015
 */
public class NodesOutputReader extends AbstractDataReader<Map<String, Map<String, Double>>[]> {

    public NodesOutputReader() {
    }

    public NodesOutputReader setFile(File f) {
        file = f;
        isGzipped = false;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Map<String, Double>>[] realReader() {
        Map<String, Map<String, Double>> perturbations = new HashMap<>();
        Map<String, Map<String, Double>> accumulators = new HashMap<>();
        Map<String, Map<String, Double>> pvs = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line;
            String[] s;
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("#")) continue;
                    s = line.split("\t", -1);
                    if (s.length == 7) {
                        String path = s[0].trim(), node = s[2].trim();
                        if (path.isEmpty() || node.isEmpty()) continue;
                        double pert = Double.parseDouble(s[4]),
                                acc = Double.parseDouble(s[5]),
                                pv = Double.parseDouble(s[6]);
                        if (pv > 1.0) pv = 1.0; //This should never happen. But Locale conversion might influence results.
                        if (!perturbations.containsKey(path)) {
                            perturbations.put(path, new HashMap<>());
                            accumulators.put(path, new HashMap<>());
                            pvs.put(path, new HashMap<>());
                        }
                        perturbations.get(path).put(node, pert);
                        accumulators.get(path).put(node, acc);
                        pvs.get(path).put(node, pv);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new Map[]{
                perturbations,
                accumulators,
                pvs
        };
    }

}
