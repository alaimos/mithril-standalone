package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.Commons.Reader.AbstractRemoteDataReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class RemoteTextFileReader extends AbstractRemoteDataReader<List<String[]>> {

    protected String separator       = "\t";
    protected int    fieldCountLimit = -1;

    public RemoteTextFileReader() {
    }

    public RemoteTextFileReader(String url) {
        setUrl(url);
    }

    public RemoteTextFileReader(String url, boolean persisted, String file) {
        setPersisted(persisted).setUrl(url).setFile(file);
    }

    public RemoteTextFileReader(String url, String file) {
        this(url, true, file);
    }

    public String getSeparator() {
        return separator;
    }

    public RemoteTextFileReader setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public int getFieldCountLimit() {
        return fieldCountLimit;
    }

    public RemoteTextFileReader setFieldCountLimit(int fieldCountLimit) {
        this.fieldCountLimit = fieldCountLimit;
        return this;
    }

    @Override
    protected List<String[]> realReader() {
        ArrayList<String[]> result = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line;
            String[] s;
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    s = line.split(separator, -1);
                    if (s.length > 0 && (fieldCountLimit < 0 || (fieldCountLimit > 0 && s.length == fieldCountLimit))) {
                        result.add(s);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
}
