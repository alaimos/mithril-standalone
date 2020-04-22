package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.Commons.Reader.AbstractRemoteDataReader;
import com.alaimos.MITHrIL.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class reads the current version of MITHrIL from our server
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class RemoteVersionReader extends AbstractRemoteDataReader<String> {

    public RemoteVersionReader() {
        init();
    }

    private void init() {
        setGzipped(false);
        setPersisted(true).setUrl(Constants.MITHRIL_VERSION_URL).setFile(Constants.MITHRIL_VERSION_FILE);
    }

    @Override
    protected String realReader() {
        //Ensures that all parameters are correctly set
        init();
        //Start Reading
        String line;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            line = r.readLine();
            if (line == null) {
                throw new RuntimeException("Cannot gather current MITHrIL version.");
            } else {
                line = line.trim();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return line;
    }
}
