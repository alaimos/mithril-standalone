package com.alaimos.Commons.Utils;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 07/12/2015
 */
public class Utils {

    public static final Pattern ENUM_PATTERN = Pattern.compile("[^a-zA-Z0-9_]+");

    /**
     * A common method for all enums since they can't have another base class
     *
     * @param <T>          Enum type
     * @param c            enum type. All enums must be all caps.
     * @param string       case insensitive
     * @param defaultValue The default value when a match is not found
     * @return corresponding enum, or null
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string, T defaultValue) {
        if (c != null && string != null) {
            try {
                string = ENUM_PATTERN.matcher(string).replaceAll("_").toUpperCase();
                return Enum.valueOf(c, string);
            } catch (IllegalArgumentException ignore) {
            }
        }
        return defaultValue;
    }

    /**
     * Get appdata directory
     * If directory does not exists it will be created
     *
     * @return the file object
     */
    public static File getAppDir() {
        File d = new File(System.getProperty("user.home"), ".mithril");
        if (!d.exists()) {
            if (!d.mkdir()) {
                throw new RuntimeException("Unable to create mithril directory");
            }
        }
        return d;
    }

    /**
     * Download a file
     *
     * @param url      the url
     * @param filename the filename
     * @return a file object
     */
    public static File download(String url, String filename) {
        return download(url, new File(getAppDir(), filename));
    }

    /**
     * Download a file
     *
     * @param url      the url
     * @param filename the file object
     * @return the file object
     */
    @Nullable
    public static File download(String url, File filename) {
        try {
            URL website = new URL(url);
            try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                 FileOutputStream fos = new FileOutputStream(filename);
            ) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                return filename;
            } catch (IOException e) {
                System.err.println(e.toString());
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

    //https://github.com/jillesvangurp/jsonj

    /**
     * Runs a checked cast from an Object to a class
     *
     * @param o      the object
     * @param aClass the class
     * @param <E>    the type of the class
     * @return the casted object or null if no cast is possible
     */
    @Nullable
    public static <E> E checkedCast(Object o, Class<E> aClass) {
        try {
            return aClass.cast(o);
        } catch (ClassCastException ignore) {
            return null;
        }
    }

    public static <E> Optional<E> checkedOptionalCast(Object o, Class<E> aClass) {
        return Optional.ofNullable(checkedCast(o, aClass));
    }

    /**
     * Returns an optional finite double value
     *
     * @param d a boxed double value
     * @return an optional double value
     */
    public static Optional<Double> optionalFiniteDouble(Double d) {
        return Optional.ofNullable(d).filter(Double::isFinite);
    }

}
