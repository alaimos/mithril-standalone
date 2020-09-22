package com.alaimos.PHENSIM.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.PHENSIM.Data.Results.PrintableMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.function.Function;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PhensimMatrixWriter<T> extends AbstractDataWriter<PrintableMatrix<T, String>> {

    private Function<T, String> convertColumn = null;

    public PhensimMatrixWriter(@Nullable Function<T, String> convertColumn) {
        this.convertColumn = convertColumn;
    }

    public PhensimMatrixWriter() {
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<PrintableMatrix<T, String>> write(@NotNull PrintableMatrix<T, String> data) {
        try (PrintStream ps = new PrintStream(getOutputStream())) {
            data.print(ps, "\t", null, convertColumn, null);
        }
        return this;
    }
}
