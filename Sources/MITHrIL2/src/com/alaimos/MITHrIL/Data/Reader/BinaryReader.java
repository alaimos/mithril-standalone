package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.Commons.Reader.AbstractDataReader;
import com.alaimos.Commons.Reader.DataReaderInterface;
import com.alaimos.Commons.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Read on object stored in binary format
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class BinaryReader<E extends Serializable> extends AbstractDataReader<E> {

    private Class<E> aClass;

    public BinaryReader(Class<E> aClass) {
        this.aClass = aClass;
        isGzipped = true;
    }

    @Override
    public DataReaderInterface<E> setFile(String f) {
        super.setFile(f);
        isGzipped = true;
        return this;
    }

    @Override
    public DataReaderInterface<E> setFile(File f) {
        super.setFile(f);
        isGzipped = true;
        return this;
    }

    @Override
    public AbstractDataReader<E> setGzipped(boolean gzipped) {
        isGzipped = true;
        return this;
    }

    @Override
    protected E realReader() {
        try (ObjectInputStream is = new ObjectInputStream(getInputStream())) {
            return Utils.checkedCast(is.readObject(), aClass);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
