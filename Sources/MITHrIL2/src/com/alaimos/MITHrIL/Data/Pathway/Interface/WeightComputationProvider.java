package com.alaimos.MITHrIL.Data.Pathway.Interface;

import java.io.Serializable;
import java.util.Map;

/**
 * This interface is used to identify all classes which provide edge weight computation procedures
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 13/01/2016
 */
public interface WeightComputationProvider extends Serializable {

    /**
     * Returns a map of edge computation procedures
     * <p>
     * The key of the map corresponds to an user-friendly name given to the computation engine, while the value is a
     * weight computation object
     *
     * @return a map
     */
    Map<String, WeightComputationInterface> getWeightComputations();

}
