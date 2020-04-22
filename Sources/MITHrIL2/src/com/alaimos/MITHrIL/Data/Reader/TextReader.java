package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.Commons.Reader.AbstractDataReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 12/12/2015
 */
public class TextReader extends AbstractDataReader<String[]> {

    public TextReader() {
        isGzipped = false;
    }

    @Override
    protected String[] realReader() {
        var l = new ArrayList<String>();
        try (var r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    l.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        var r = new String[l.size()];
        return l.toArray(r);
    }
}
