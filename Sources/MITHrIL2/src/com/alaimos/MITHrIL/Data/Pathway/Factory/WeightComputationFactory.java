package com.alaimos.MITHrIL.Data.Pathway.Factory;

import com.alaimos.MITHrIL.Data.Pathway.Interface.WeightComputationInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.WeightComputationProvider;

import java.io.Serializable;
import java.util.HashMap;
import java.util.ServiceLoader;

/**
 * This objects holds all edge weight computation procedures
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 13/01/2016
 */
public class WeightComputationFactory implements Serializable {

    private static final long serialVersionUID = 8945656909092804836L;
    private static ServiceLoader<WeightComputationProvider> loader;
    private static HashMap<String, WeightComputationInterface> availableComputations = new HashMap<>();

    static {
        availableComputations.put("default", (e, ed) -> ed.getSubType().weight());
        loader = ServiceLoader.load(WeightComputationProvider.class);
        loader.forEach(s -> availableComputations.putAll(s.getWeightComputations()));
    }

    /**
     * Get the default weight computation procedure
     *
     * @return the default weight computation procedure
     */
    public static WeightComputationInterface getWeightComputation() {
        return getWeightComputation("default");
    }

    /**
     * Get an edge weight computation procedure
     *
     * @param name the user-friendly name
     * @return the weight computation procedure
     */
    public static WeightComputationInterface getWeightComputation(String name) {
        if (name == null) name = "default";
        if (!availableComputations.containsKey(name)) name = "default";
        return availableComputations.get(name);
    }

}
