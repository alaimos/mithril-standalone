package com.alaimos.Commons.Reader;

import com.alaimos.Commons.Utils.Utils;

import java.util.Date;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public abstract class AbstractRemoteDataReader<T> extends AbstractDataReader<T> implements RemoteDataReaderInterface<T> {

    protected static long maxTimeCache = 86400; // 1 day
    protected static long limit        = new Date().getTime() - maxTimeCache * 1000;

    protected String  url;
    protected boolean persisted = true;

    public static long getMaxTimeCache() {
        return maxTimeCache;
    }

    public static void setMaxTimeCache(long maxTimeCache) {
        AbstractRemoteDataReader.maxTimeCache = maxTimeCache;
        AbstractRemoteDataReader.limit = new Date().getTime() - maxTimeCache * 1000;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public RemoteDataReaderInterface<T> setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public boolean isPersisted() {
        return this.persisted;
    }

    @Override
    public RemoteDataReaderInterface<T> setPersisted(boolean persisted) {
        this.persisted = persisted;
        return this;
    }

    @Override
    public T read() {
        if (file == null) this.setFile();
        if (!file.exists() || !persisted || (file.lastModified() < limit)) {
            if (url == null || url.isEmpty()) {
                throw new RuntimeException("URL is empty!");
            }
            Utils.download(this.url, file);
        }
        T result = realReader();
        if (!persisted) {
            boolean ignore = file.delete();
        }
        return result;
    }
}
