package com.alaimos.MITHrIL.Utils;

import com.alaimos.Commons.Utils.Utils;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import org.junit.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 11/12/2015
 */
public class UtilsTest {

    @Test
    public void testGetEnumFromString() throws Exception {
        assertNotNull(EdgeSubType.fromString("activation"));
        assertEquals(EdgeSubType.valueOf("BINDING_ASSOCIATION"), EdgeSubType.fromString("binding/association"));
    }

    @Test
    public void testGetAppDir() throws Exception {
        File d = Utils.getAppDir();
        assertTrue(d.exists());
    }

    @Test
    public void testDownload() throws Exception {
        File f = Utils.download("http://alpha.dmi.unict.it/mithril/data/pathways/hsa.txt.gz", "out.txt.gz");
        assertNotNull(f);
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testCheckedCast() throws Exception {
        Object o1 = "This is a test string", o2 = 10, o3 = null;
        String s1 = Utils.checkedCast(o1, String.class), s2 = Utils.checkedCast(o2, String.class),
                s3 = Utils.checkedCast(o3, String.class);
        assertNotNull(s1);
        assertNull(s2);
        assertEquals("This is a test string", s1);
        assertNull(s3);
    }

    @Test
    public void testOptionalFiniteDouble() throws Exception {
        Double d1 = 10d, d2 = Double.NaN, d3 = Double.POSITIVE_INFINITY;
        Optional<Double> od1 = Utils.optionalFiniteDouble(d1),
                od2 = Utils.optionalFiniteDouble(d2),
                od3 = Utils.optionalFiniteDouble(d3);
        assertTrue(od1.isPresent());
        assertFalse(od2.isPresent());
        assertFalse(od3.isPresent());
        assertTrue(od1.get() == 10d);
    }
}