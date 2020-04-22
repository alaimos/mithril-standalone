package com.alaimos.MITHrIL.Data.Pathway.Type;

import com.alaimos.Commons.Utils.Utils;
import com.alaimos.MITHrIL.Constants;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/12/2015
 */
public class NodeType extends TextFileDynamicEnum {
    private static final long serialVersionUID = -7450659007204386050L;

    static {
        File f = getFileObject(NodeType.class);
        if (!f.exists()) {
            Utils.download(Constants.COMMONS_NODE_TYPE, f);
        }
        init(NodeType.class);
    }
    /*
    GENE,
    COMPOUND,
    MAP,
    REACTION,
    MIRNA,
    ENZYME,
    OTHER;*/

    private double sign = 0.0;

    protected NodeType(int ordinal, String name) {
        super(ordinal, name);
    }

    protected NodeType(int ordinal, String name, String[] others) {
        super(ordinal, name);
        if (others.length >= 1) {
            sign = Double.parseDouble(others[0]);
        }
    }

    public double sign() {
        return sign;
    }

    public static NodeType valueOf(String name) {
        return (NodeType) valueOf(NodeType.class, name);
    }

    public static NodeType[] values() {
        return values(NodeType.class);
    }

    public static NodeType fromString(String name) {
        return (NodeType) fromString(NodeType.class, name, "OTHER");
    }

    /**
     * Add a new element to this enum
     *
     * @param name the name of the new element
     * @return the added element
     */
    public static NodeType add(String name) {
        return add(NodeType.class, name);
    }

    /**
     * Add a new element to this enum
     *
     * @param name   the name of the new element
     * @param others other parameters for the element
     * @return the added element
     */
    @Nullable
    public static NodeType add(String name, String[] others) {
        return add(NodeType.class, name, others);
    }

    /**
     * Add a new node type
     *
     * @param name the name of this type
     * @param sign the sign used for the accumulator computation
     * @return the added type
     */
    @Nullable
    public static NodeType add(String name, double sign) {
        return add(NodeType.class, name, new String[]{Double.toString(sign)});
    }

}
