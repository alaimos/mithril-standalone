package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.MITHrIL.Data.Records.Species;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class RemoteSpeciesDatabaseReaderTest {

    @Test
    public void testReadSpecies() throws Exception {
        HashMap<String, Species> s = RemoteSpeciesDatabaseReader.getInstance().readSpecies();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }
}