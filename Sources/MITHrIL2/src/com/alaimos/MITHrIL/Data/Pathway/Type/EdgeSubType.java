package com.alaimos.MITHrIL.Data.Pathway.Type;

import com.alaimos.Commons.Utils.Utils;
import com.alaimos.MITHrIL.Constants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/12/2015
 */
public class EdgeSubType extends TextFileDynamicEnum {
    private static final long serialVersionUID = 3014690592935302878L;

    static {
        File f = getFileObject(EdgeSubType.class);
        if (!f.exists()) {
            Utils.download(Constants.COMMONS_EDGE_SUBTYPE, f);
        }
        init(EdgeSubType.class);
    }
    /*COMPOUND(1, 1),
    HIDDEN_COMPOUND(1, 0),     // If u->v compound has priority over u->v hidden compound
    ACTIVATION(1, 2),
    INHIBITION(-1, 2),
    EXPRESSION(1, 3),
    REPRESSION(-1, 3),
    INDIRECT_EFFECT(1, 0),     // Direct Effects between two nodes have priority over Indirect Effects
    STATE_CHANGE(0, 2),
    BINDING_ASSOCIATION(1, 2),
    DISSOCIATION(-1, 2),
    MISSING_INTERACTION(0, 4), //Missing interactions have priority over other interactions because due to mutation
    //such connection is lost.
    PHOSPHORYLATION(0, 2),
    DEPHOSPHORYLATION(0, 2),
    GLYCOSYLATION(0, 2),
    UBIQUITINATION(0, 2),
    METHYLATION(0, 2),
    MIRNA_INHIBITION(-1, 3),   //It should never happen that u->v is simultaneously of this type and another one.
    TFMIRNA_ACTIVATION(1, 3),  //It should never happen that u->v is simultaneously of this type and another one.
    TFMIRNA_INHIBITION(-1, 3), //It should never happen that u->v is simultaneously of this type and another one.
    UNKNOWN(0, 0);             //Unknown interactions are kept only if there is nothing else*/

    private double weight   = 0.0;
    private int    priority = 0;
    private String symbol   = "";

    protected EdgeSubType(int ordinal, String name) {
        super(ordinal, name);
    }

    protected EdgeSubType(int ordinal, String name, String[] others) {
        super(ordinal, name);
        if (others.length >= 1) {
            weight = Double.parseDouble(others[0]);
            if (others.length >= 2) {
                priority = Integer.parseInt(others[1]);
                if (others.length >= 3) {
                    symbol = others[2];
                }
            }
        }
    }

    public static EdgeSubType valueOf(String name) {
        return (EdgeSubType) valueOf(EdgeSubType.class, name);
    }

    public static EdgeSubType[] values() {
        return values(EdgeSubType.class);
    }

    public static EdgeSubType fromString(String name) {
        return (EdgeSubType) fromString(EdgeSubType.class, name, "UNKNOWN");
    }

    @Contract(pure = true)
    public double weight() {
        return weight;
    }

    @Contract(pure = true)
    public int priority() {
        return priority;
    }

    @Contract(pure = true)
    public String symbol() {
        return symbol;
    }

    /**
     * Add a new element to this enum
     *
     * @param name the name of the new element
     * @return the added element
     */
    public static EdgeSubType add(String name) {
        return add(EdgeSubType.class, name);
    }

    /**
     * Add a new element to this enum
     *
     * @param name   the name of the new element
     * @param others other parameters for the element
     * @return the added element
     */
    @Nullable
    public static EdgeSubType add(String name, String[] others) {
        return add(EdgeSubType.class, name, others);
    }

    /**
     * Add a new edge SubType
     *
     * @param name     the name of the subtype
     * @param weight   the weight of this subtype
     * @param priority the priority of this subtype
     * @param symbol   the symbol that represents this subtype
     * @return the added subtype
     */
    @Nullable
    public static EdgeSubType add(String name, double weight, int priority, String symbol) {
        return add(EdgeSubType.class, name, new String[]{
                Double.toString(weight),
                Integer.toString(priority),
                symbol
        });
    }

}
