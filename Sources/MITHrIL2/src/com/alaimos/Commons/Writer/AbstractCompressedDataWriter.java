package com.alaimos.Commons.Writer;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/01/2016
 */
public abstract class AbstractCompressedDataWriter<T> extends AbstractDataWriter<T> implements DataWriterInterface<T> {

    protected OutputStream getOutputStream(boolean append) {
        try {
            return new GZIPOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
