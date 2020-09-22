package com.alaimos.PHENSIM.Data.Reader;

import com.alaimos.Commons.Reader.AbstractDataReader;
import com.alaimos.PHENSIM.Data.Generator.ExpressionConstraint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static com.alaimos.PHENSIM.Data.Generator.ExpressionConstraint.BOTH;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class SIMPATHYInputReader extends AbstractDataReader<Map<String, ExpressionConstraint>> {

    public SIMPATHYInputReader() {
    }

    public SIMPATHYInputReader setFile(File f) {
        file = f;
        isGzipped = false;
        return this;
    }

    @Override
    protected Map<String, ExpressionConstraint> realReader() {
        HashMap<String, ExpressionConstraint> result = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line;
            String[] s;
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("#")) continue;
                    s = line.split("\t", -1);
                    if (s.length > 0) {
                        String node = s[0].trim();
                        if (node.isEmpty()) continue;
                        ExpressionConstraint constraint = BOTH;
                        if (s.length >= 2) {
                            constraint = ExpressionConstraint.fromString(s[1]);
                        }
                        result.put(node, constraint);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
}
