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
public class MainOutputReader extends AbstractDataReader<Map<String, Double>[]> {

    public MainOutputReader() {
    }

    public MainOutputReader setFile(File f) {
        file = f;
        isGzipped = false;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Double>[] realReader() {
        Map<String, Double> accumulators = new HashMap<>();
        Map<String, Double> impactFactors = new HashMap<>();
        Map<String, Double> probabilities = new HashMap<>();
        Map<String, Double> probabilitiesNetwork = new HashMap<>();
        Map<String, Double> correctedAccumulators = new HashMap<>();
        Map<String, Double> pValues = new HashMap<>();
        Map<String, Double> adjustedPValues = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line;
            String[] s;
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("#")) continue;
                    s = line.split("\t", -1);
                    if (s.length == 8 || s.length == 9) {
                        String path = s[0].trim();
                        if (path.isEmpty()) continue;
                        accumulators.put(path, Double.parseDouble(s[2]));
                        impactFactors.put(path, Double.parseDouble(s[3]));
                        probabilities.put(path, Double.parseDouble(s[4]));
                        if (s.length == 9) {
                            probabilitiesNetwork.put(path, Double.parseDouble(s[5]));
                            correctedAccumulators.put(path, Double.parseDouble(s[6]));
                            pValues.put(path, Double.parseDouble(s[7]));
                            adjustedPValues.put(path, Double.parseDouble(s[8]));
                        } else {
                            probabilitiesNetwork.put(path, Double.NaN);
                            correctedAccumulators.put(path, Double.parseDouble(s[5]));
                            pValues.put(path, Double.parseDouble(s[6]));
                            adjustedPValues.put(path, Double.parseDouble(s[7]));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return (Map<String, Double>[]) new Map[]{
                accumulators,
                impactFactors,
                probabilities,
                correctedAccumulators,
                pValues,
                adjustedPValues,
                probabilitiesNetwork
        };
    }
}
