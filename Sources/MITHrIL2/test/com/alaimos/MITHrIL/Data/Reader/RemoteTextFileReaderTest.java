package com.alaimos.MITHrIL.Data.Reader;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class RemoteTextFileReaderTest {

    @Test
    public void testRead() throws Exception {
        RemoteTextFileReader rf = new RemoteTextFileReader("https://alpha.dmi.unict.it/mithril/data/index.txt.gz");
        List<String[]> result = rf.read();
        assertNotNull(result);
        assertTrue(result.size() > 0);
        result.stream().map(r -> r.length).forEach(i -> assertTrue(i > 0));
        assertFalse((new File(rf.getFile())).exists());
    }

    @Test
    public void testReadLimitedFields() throws Exception {
        RemoteTextFileReader rf = new RemoteTextFileReader("https://alpha.dmi.unict.it/mithril/data/index.txt.gz");
        rf.setFieldCountLimit(7);
        List<String[]> result = rf.read();
        assertNotNull(result);
        assertTrue(result.size() > 0);
        result.stream().map(r -> r.length).forEach(i -> assertTrue(i == 7));
        assertFalse((new File(rf.getFile())).exists());
    }

    @Test
    public void testReadLimitedFields1() throws Exception {
        RemoteTextFileReader rf = new RemoteTextFileReader("https://alpha.dmi.unict.it/mithril/data/index.txt.gz");
        rf.setFieldCountLimit(6);
        List<String[]> result = rf.read();
        assertNotNull(result);
        assertTrue(result.size() == 0);
        assertFalse((new File(rf.getFile())).exists());
    }

    @Test
    public void testPersistentRead() throws Exception {
        RemoteTextFileReader rf =
                new RemoteTextFileReader("https://alpha.dmi.unict.it/mithril/data/index.txt.gz", "index.txt.gz");
        List<String[]> result = rf.read();
        assertNotNull(result);
        assertTrue(result.size() > 0);
        result.stream().map(r -> r.length).forEach(i -> assertTrue(i > 0));
        File f = new File(rf.getFile());
        assertTrue(f.exists());
        assertTrue(f.delete());
    }
}