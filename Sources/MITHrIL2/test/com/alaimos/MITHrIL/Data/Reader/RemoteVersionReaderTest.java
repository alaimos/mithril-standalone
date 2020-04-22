package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.MITHrIL.Constants;
import com.alaimos.Commons.Utils.Version;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class RemoteVersionReaderTest {

    @Test
    public void testRead() throws Exception {
        RemoteVersionReader r = new RemoteVersionReader();
        String version = r.read();
        assertTrue(Version.compare(version).with(Constants.CURRENT_VERSION) >= 0);
    }
}