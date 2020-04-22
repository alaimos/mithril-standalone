package com.alaimos.Commons.Reader;

import com.alaimos.Commons.Utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public abstract class AbstractDataReader<T> implements DataReaderInterface<T> {

    protected boolean isGzipped = true;
    protected File file;

    @Override
    public DataReaderInterface<T> setFile(String f) {
        file = new File(Utils.getAppDir(), f);
        return this;
    }

    @Override
    public DataReaderInterface<T> setFile(File f) {
        file = f;
        return this;
    }

    @Override
    public String getFile() {
        return file.getAbsolutePath();
    }

    public boolean isGzipped() {
        return isGzipped;
    }

    public AbstractDataReader<T> setGzipped(boolean gzipped) {
        isGzipped = gzipped;
        return this;
    }

    protected InputStream getInputStream() {
        try {
            InputStream f = new FileInputStream(file);
            if (isGzipped) {
                f = new GZIPInputStream(f);
            }
            return f;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected abstract T realReader();

    @Override
    public T read() {
        if (file == null || (!file.exists())) {
            throw new RuntimeException("Filename is not set or file does not exists.");
        }
        return realReader();
    }
}
