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
public class ExpressionMapReader extends AbstractDataReader<Map<String, Double>> {

    public ExpressionMapReader() {
    }

    public ExpressionMapReader setFile(File f) {
        file = f;
        isGzipped = false;
        return this;
    }

    @Override
    protected Map<String, Double> realReader() {
        HashMap<String, Double> result = new HashMap<>();
        try (var r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line;
            String[] s;
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("#")) continue;
                    s = line.split("\t", -1);
                    if (s.length > 0) {
                        String node = s[0].trim();
                        if (node.isEmpty()) continue;
                        var value = 0.0;
                        if (s.length >= 2 && !s[1].equalsIgnoreCase("null")) {
                            try {
                                value = Double.parseDouble(s[1]);
                            } catch (NumberFormatException | NullPointerException ignored) {
                                value = 0.0;
                            }
                            if (Double.isNaN(value)) value = 0.0;
                        }
                        result.put(node, value);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
}
