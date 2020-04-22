package com.alaimos.MITHrIL.Utils;

import org.junit.Test;

import static com.alaimos.Commons.Utils.Version.compare;
import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class VersionTest {

    @Test
    public void testCompare() {
        assertEquals(-1, compare("1.0.1").with("1.0.2"));
        assertEquals(1, compare("1.0.2.1").with("1.0.2"));
        assertTrue(compare("1.0.2").eq("1.0.2"));
    }

    @Test
    public void testApproximation() {
        assertTrue(compare("1.0.2").agt("1.0")); // ~> 1.0 => >= 1.0 && < 2.0
        assertFalse(compare("2.0").agt("1.0"));
        assertTrue(compare("1.9").agt("1.0")); // ~> 1.0 => >= 1.0 && < 2.0
        assertFalse(compare("0.9").agt("1.0"));
        assertTrue(compare("1.0.2").agt("1.0.2")); // ~> 1.0.2 => >= 1.0.2 && < 1.1
        assertFalse(compare("1.2").agt("1.0.2"));
    }

}