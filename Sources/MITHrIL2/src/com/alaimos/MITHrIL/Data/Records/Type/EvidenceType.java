package com.alaimos.MITHrIL.Data.Records.Type;

import com.alaimos.Commons.Utils.Utils;
import org.jetbrains.annotations.Contract;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 06/12/2015
 * @version 2.0.0.0
 */
public enum EvidenceType {
    STRONG(0),
    WEAK(1),
    PREDICTION(2),
    UNKNOWN(3);

    private int value;

    private EvidenceType(int value) {
        this.value = value;
    }

    @Contract(pure = true)
    public int value() {
        return value;
    }

    public static EvidenceType fromString(String name) {
        return Utils.getEnumFromString(EvidenceType.class, name, UNKNOWN);
    }
}
