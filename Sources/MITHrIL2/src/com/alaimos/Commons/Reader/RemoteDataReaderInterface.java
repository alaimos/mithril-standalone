package com.alaimos.Commons.Reader;

import java.util.Date;
import java.util.UUID;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public interface RemoteDataReaderInterface<T> extends DataReaderInterface<T> {

    /**
     * Get the url where data can be found
     *
     * @return the url
     */
    String getUrl();

    /**
     * Set the url where data can be found
     *
     * @param url the url
     * @return this object
     */
    RemoteDataReaderInterface<T> setUrl(String url);

    /**
     * Is the local file temporary or it is kept as cache for a certain time?
     *
     * @return is the local file kept as cache?
     */
    boolean isPersisted();

    /**
     * Set if the local file is temporary or is kept as cache for a certain time
     *
     * @param persisted is the local file kept as cache?
     * @return this object
     */
    RemoteDataReaderInterface<T> setPersisted(boolean persisted);

    /**
     * Set a temporary filename
     *
     * @return this object
     */
    default DataReaderInterface<T> setFile() {
        return setPersisted(false).setFile("tmp_" + new Date().getTime() + "_" + UUID.randomUUID());
    }

    /**
     * Set and read a URL
     * @param u the url
     * @return the result
     */
    @Override
    default T read(String u) {
        return setUrl(u).read();
    }

    /**
     * Download an URL in a file and reads its content
     * @param u the url
     * @param f the file
     * @return the result
     */
    default T read(String u, String f) {
        return setUrl(u).setFile(f).read();
    }

}
