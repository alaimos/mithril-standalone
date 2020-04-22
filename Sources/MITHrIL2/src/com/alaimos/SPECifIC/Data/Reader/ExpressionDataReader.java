package com.alaimos.SPECifIC.Data.Reader;

import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.DataMatrix;
import com.alaimos.Commons.Reader.AbstractDataReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * SPECifIC
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class ExpressionDataReader extends AbstractDataReader<DataMatrix> {

    public ExpressionDataReader() {
    }

    public ExpressionDataReader setFile(File f) {
        file = f;
        isGzipped = true;
        return this;
    }

    protected static String convertName(String name) {
        return name.replace("hsa:", "").replace("hsa-mir", "hsa-miR");
    }

    @Override
    protected DataMatrix realReader() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<double[]> expressions = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line = r.readLine(), name;
            String[] s = line.split("\t", -1);
            double[] e;
            int numberOfColumns = (s.length + 1);
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    s = line.split("\t", -1);
                    if (s.length > 0 && s.length == numberOfColumns) {
                        name = s[0].trim();
                        e = new double[numberOfColumns];
                        for (int i = 1; i < s.length; i++) {
                            e[i - 1] = Double.parseDouble(s[i]);
                        }
                        names.add(convertName(name));
                        expressions.add(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        String[] namesArray = names.toArray(new String[names.size()]);
        double[][] expressionsArray = expressions.toArray(new double[expressions.size()][]);
        return new DataMatrix(namesArray, expressionsArray);
    }
}
