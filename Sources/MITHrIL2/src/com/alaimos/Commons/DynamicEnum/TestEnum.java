package com.alaimos.Commons.DynamicEnum;

import com.alaimos.MITHrIL.Data.Pathway.Type.TextFileDynamicEnum;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 29/12/2015
 */
public class TestEnum extends TextFileDynamicEnum {
    static {
        init(TestEnum.class);
    }

    private String first;
    private int    second;

    /**
     * Constructor.  Programmers must invoke this constructor.
     *
     * @param ordinal The ordinal of this enumeration constant (its position in the dynamicEnum declaration, where the
     * @param name    The name of this dynamicEnum constant, which is the identifier used to declare it.
     */
    protected TestEnum(int ordinal, String name) {
        super(ordinal, name);
    }

    protected TestEnum(int ordinal, String name, String[] others) {
        super(ordinal, name);
        first = others[0];
        second = Integer.parseInt(others[1]);
    }

    public String first() {
        return first;
    }

    public int second() {
        return second;
    }

    public static TestEnum valueOf(String name) {
        return (TestEnum) valueOf(TestEnum.class, name);
    }

    public static TestEnum[] values() {
        return values(TestEnum.class);
    }

    public static TestEnum fromString(String name) {
        return (TestEnum) fromString(TestEnum.class, name, "E");
    }
}
