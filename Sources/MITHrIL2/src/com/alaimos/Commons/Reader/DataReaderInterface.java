package com.alaimos.Commons.Reader;

import java.io.File;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public interface DataReaderInterface<T> {

    /**
     * Get the filename where data are stored
     *
     * @return the filename
     */
    String getFile();

    /**
     * Set the filename where data are stored
     *
     * @param f the filename
     * @return this object
     */
    DataReaderInterface<T> setFile(String f);

    /**
     * Set the file where data are stored
     *
     * @param f the filename
     * @return this object
     */
    DataReaderInterface<T> setFile(File f);

    /**
     * Read data
     *
     * @return the result
     */
    T read();


    /**
     * Set filename and read data from it
     *
     * @param f the filename
     * @return the result
     */
    default T read(File f) {
        return setFile(f).read();
    }

    /**
     * Set filename and read data from it
     *
     * @param f the filename
     * @return the result
     */
    default T read(String f) {
        return setFile(f).read();
    }

}
