package com.alaimos.MITHrIL.Data.Pathway.Interface;

import java.io.Serializable;

/**
 * Compute a weight for each edge in a pathway
 *
 * @author Salvatore Alaimo, Ph.D.
 * @since 06/12/2015
 * @version 2.0.0.0
 */
@FunctionalInterface
public interface WeightComputationInterface extends Cloneable, Serializable {

    double weight(EdgeInterface e, EdgeDescriptionInterface ed);

}
