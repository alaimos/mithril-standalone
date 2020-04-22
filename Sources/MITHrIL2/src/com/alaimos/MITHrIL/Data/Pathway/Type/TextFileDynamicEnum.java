package com.alaimos.MITHrIL.Data.Pathway.Type;

import com.alaimos.Commons.DynamicEnum.DynamicEnum;
import com.alaimos.Commons.Utils.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 29/12/2015
 */
public abstract class TextFileDynamicEnum extends DynamicEnum<TextFileDynamicEnum> {

    private static final Class[] baseStructure       = new Class[]{int.class, String.class};
    private static final Class[] additionalStructure = new Class[]{int.class, String.class, String[].class};
    private static final long    serialVersionUID    = -3426398283283595168L;

    /**
     * Constructor.  Programmers must invoke this constructor.
     *
     * @param name    The name of this dynamicEnum constant, which is the identifier used to declare it.
     * @param ordinal The ordinal of this enumeration constant (its position in the dynamicEnum declaration, where the
     */
    protected TextFileDynamicEnum(int ordinal, String name) {
        super(ordinal, name);
    }

    public static <T extends DynamicEnum<T>> T valueOf(String name) {
        return valueOf(TextFileDynamicEnum.class, name);
    }

    public static <E> DynamicEnum<? extends DynamicEnum<?>>[] values() {
        return values(TextFileDynamicEnum.class);
    }

    protected static <E> void init(Class<E> clazz) {
        try {
            initProps(clazz);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Contract("_ -> !null")
    static File getFileObject(Class<?> clazz) {
        return new File(Utils.getAppDir(), clazz.getSimpleName().replace('.', '/').toLowerCase() + ".enum");
    }

    private static <E> void initProps(Class<E> clazz) throws Exception {
        File f = getFileObject(clazz);
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            Constructor<E> minimalConstructor = getConstructor(clazz, baseStructure);
            assert minimalConstructor != null;
            Constructor<E> additionalConstructor = getConstructor(clazz, additionalStructure);
            int ordinal = 0;
            String[] parts;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                parts = line.split("\t", -1);
                if (parts.length == 1 || additionalConstructor == null) {
                    minimalConstructor.newInstance(ordinal, parts[0]);
                } else if (parts.length > 1) {
                    additionalConstructor.newInstance(ordinal, parts[0], Arrays.copyOfRange(parts, 1, parts.length));
                }
                ordinal++;
            }
        }
    }

    /**
     * Add a new element to this enum
     *
     * @param clazz a class object
     * @param name  the name of the new element
     * @param <E>   the class of the enum
     * @return the added element
     */
    public static <E> E add(Class<E> clazz, String name) {
        return add(clazz, name, null);
    }

    /**
     * Add a new element to this enum
     *
     * @param clazz  the class object of this enum
     * @param name   the name of the new element
     * @param others other parameters for the element
     * @param <E>    the class of the enum
     * @return the added element
     */
    @Nullable
    public static <E> E add(Class<E> clazz, String name, String[] others) {
        Constructor<E> minimalConstructor = getConstructor(clazz, baseStructure);
        assert minimalConstructor != null;
        Constructor<E> additionalConstructor = getConstructor(clazz, additionalStructure);
        int ordinal = lastOrd.getOrDefault(clazz, -1);
        ++ordinal;
        try {
            if (others != null && additionalConstructor != null) {
                return additionalConstructor.newInstance(ordinal, name, others);
            } else {
                return minimalConstructor.newInstance(ordinal, name);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <E> Constructor<E> getConstructor(Class<E> clazz, Class<?>[] argTypes) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            try {
                return (Constructor<E>) c.getDeclaredConstructor(argTypes);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
