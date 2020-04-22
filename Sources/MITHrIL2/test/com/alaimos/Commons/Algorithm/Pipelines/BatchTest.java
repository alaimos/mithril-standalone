package com.alaimos.Commons.Algorithm.Pipelines;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 30/12/2015
 */
public class BatchTest {

    @Test
    public void testRun() throws Exception {
        Batch<Integer> b = new Batch<>(BatchTestAlgorithm.class);
        List<Map<String, Object>> batches = IntStream.range(1, 101).mapToObj(value -> {
            HashMap<String, Object> tmp = new HashMap<>();
            tmp.put("value", value * 2);
            return tmp;
        }).collect(Collectors.toList());
        HashMap<String, Object> params = new HashMap<>();
        params.put("batch", batches);
        b.init().setParameters(params).run();
        Map<Integer, Integer> results = b.getOutput();
        assertNotNull(results);
        assertEquals(100, results.size());
        HashMap<Integer, Integer> correctResults = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            correctResults.put(i, IntStream.rangeClosed(1, (i + 1) * 2).sum());
        }
        assertEquals(correctResults, results);
    }
}