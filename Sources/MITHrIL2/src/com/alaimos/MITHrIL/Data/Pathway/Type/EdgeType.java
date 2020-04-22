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
public class EdgeType extends TextFileDynamicEnum {
    private static final long serialVersionUID = -5966317971718831039L;

    static {
        File f = getFileObject(EdgeType.class);
        if (!f.exists()) {
            Utils.download(Constants.COMMONS_EDGE_TYPE, f);
        }
        init(EdgeType.class);
    }
    /*
    ECREL,
    PPREL,
    GEREL,
    PCREL,
    MGREL,
    MAPLINK,
    OTHER;*/

    protected EdgeType(int ordinal, String name) {
        super(ordinal, name);
    }

    public static EdgeType valueOf(String name) {
        return (EdgeType) valueOf(EdgeType.class, name);
    }

    public static EdgeType[] values() {
        return values(EdgeType.class);
    }

    public static EdgeType fromString(String name) {
        return (EdgeType) fromString(EdgeType.class, name, "OTHER");
    }

    /**
     * Add a new element to this enum
     *
     * @param name the name of the new element
     * @return the added element
     */
    public static EdgeType add(String name) {
        return add(EdgeType.class, name);
    }

    /**
     * Add a new element to this enum
     *
     * @param name   the name of the new element
     * @param others other parameters for the element
     * @return the added element
     */
    @Nullable
    public static EdgeType add(String name, String[] others) {
        return add(EdgeType.class, name, others);
    }

}
