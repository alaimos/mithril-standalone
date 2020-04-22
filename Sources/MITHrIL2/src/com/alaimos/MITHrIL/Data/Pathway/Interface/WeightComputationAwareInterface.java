package com.alaimos.MITHrIL.Data.Pathway.Interface;

import com.alaimos.MITHrIL.Data.Pathway.Factory.WeightComputationFactory;

import java.io.Serializable;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 13/01/2016
 */
public interface WeightComputationAwareInterface<E> extends Serializable {

    default E setDefaultWeightComputation() {
        return setDefaultWeightComputation(WeightComputationFactory.getWeightComputation());
    }

    default E setDefaultWeightComputation(String defaultWeightComputation) {
        return setDefaultWeightComputation(WeightComputationFactory.getWeightComputation(defaultWeightComputation),
                true);
    }

    default E setDefaultWeightComputation(WeightComputationInterface defaultWeightComputation) {
        return setDefaultWeightComputation(defaultWeightComputation, true);
    }

    default E setDefaultWeightComputation(String defaultWeightComputation, boolean changeAll) {
        return setDefaultWeightComputation(WeightComputationFactory.getWeightComputation(defaultWeightComputation),
                changeAll);
    }

    E setDefaultWeightComputation(WeightComputationInterface defaultWeightComputation, boolean changeAll);

    WeightComputationInterface getDefaultWeightComputation();

}
