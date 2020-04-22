package com.alaimos.MITHrIL.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPOutputStream;

/**
 * Write an object in binary format
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class BinaryWriter<E extends Serializable> extends AbstractDataWriter<E> {

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<E> write(E data) {
        try (ObjectOutputStream os = new ObjectOutputStream(new GZIPOutputStream(getOutputStream()))) {
            os.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
