package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class RemotePathwayRepositoryReaderTest {

    @Test
    public void testRead() throws Exception {
        RemotePathwayRepositoryReader rp =
                new RemotePathwayRepositoryReader("https://alpha.dmi.unict.it/mithril/data/pathways/hsa.txt.gz");
        RepositoryInterface r = rp.read();
        assertNotNull(r);
        assertTrue(r.size() > 0);
    }
}