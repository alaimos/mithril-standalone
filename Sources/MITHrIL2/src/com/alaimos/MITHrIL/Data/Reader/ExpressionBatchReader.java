package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.Commons.Reader.AbstractDataReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.1
 * @since 12/12/2015
 */
public class ExpressionBatchReader extends AbstractDataReader<Map<String, Map<String, Double>>> {

    public ExpressionBatchReader() {
    }

    public ExpressionBatchReader setFile(File f) {
        file = f;
        isGzipped = false;
        return this;
    }

    @Override
    protected Map<String, Map<String, Double>> realReader() {
        HashMap<Integer, String> map = new HashMap<>();
        Map<String, Map<String, Double>> result = new HashMap<>();
        try (var r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            var line = r.readLine();
            if (line == null) throw new RuntimeException("Invalid input file: file is empty");
            var s = line.split("\t", -1);
            for (int i = 0; i < s.length; i++) {
                s[i] = s[i].replaceAll("[^a-zA-Z0-9.\\-]", "_").trim();
                if (s[i].isEmpty()) throw new RuntimeException("Invalid input file: empty experiment name found");
                map.put((i + 1), s[i]);
                result.put(s[i], new HashMap<>());
            }
            var nextLinesSize = map.size() + 1;
            double value;
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("#")) continue;
                    s = line.split("\t", -1);
                    if (s.length == nextLinesSize) {
                        String node = s[0].trim();
                        if (node.isEmpty()) continue;
                        for (int i = 1; i < s.length; i++) {
                            s[i] = s[i].trim();
                            value = 0.0;
                            if (!s[i].isEmpty() && !s[i].equalsIgnoreCase("null")) {
                                try {
                                    value = Double.parseDouble(s[i]);
                                } catch (NumberFormatException | NullPointerException ignored) {
                                    value = 0.0;
                                }
                                if (Double.isNaN(value)) value = 0.0;
                            }
                            result.get(map.get(i)).put(node, value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
}
