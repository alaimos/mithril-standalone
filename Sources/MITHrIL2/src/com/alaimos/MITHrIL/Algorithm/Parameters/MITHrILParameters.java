package com.alaimos.MITHrIL.Algorithm.Parameters;

import com.alaimos.Commons.Algorithm.Impl.AbstractParameters;
import com.alaimos.MITHrIL.Algorithm.MITHrIL;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 21/04/16
 */
public class MITHrILParameters extends AbstractParameters<MITHrIL> {

    /**
     * An array of allowed parameters
     *
     * @return an array of parameters name
     */
    @Override
    protected String[] allowedParameters() {
        return new String[]{
                "expressions",
                "repository",
                "random",
                "numberOfRepetitions",
                "probabilityComputation",
                "pValueCombiner",
                "pValueAdjuster"
        };
    }
}
