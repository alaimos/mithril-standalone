package com.alaimos.PHENSIM.Data.Generator;

import com.alaimos.Commons.Utils.Utils;

/**
 * This enum indicate supported expression changes (over-expression, under-expression, both)
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public enum ExpressionConstraint {
    OVEREXPRESSION,
    UNDEREXPRESSION,
    BOTH;

    public static ExpressionConstraint fromString(String string) {
        return Utils.getEnumFromString(ExpressionConstraint.class, string, BOTH);
    }
}
