package com.alaimos.PHENSIM.Data.Results;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PrintableMatrix<C extends Object, R extends Object> {

    private final int rows;
    private final int columns;
    private final double[][] data;
    private C[] columnNames = null;
    private R[] rowNames = null;

    public PrintableMatrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        data = new double[rows][columns];
    }

    /**
     * Set the name of columns
     *
     * @param columnNames the name of columns
     * @return this object for a fluent interface
     */
    public PrintableMatrix<C, R> setColumnNames(@Nullable C[] columnNames) {
        if (columnNames == null) {
            this.columnNames = null;
        } else {
            if (columnNames.length != columns)
                throw new ArrayIndexOutOfBoundsException("The number of column names does not correspond with the size of this matrix");
            this.columnNames = Arrays.copyOf(columnNames, columns);
        }
        return this;
    }

    /**
     * Set the name of columns
     *
     * @param rowNames the name of columns
     * @return this object for a fluent interface
     */
    public PrintableMatrix<C, R> setRowNames(@Nullable R[] rowNames) {
        if (rowNames == null) {
            this.rowNames = null;
        } else {
            if (rowNames.length != rows)
                throw new ArrayIndexOutOfBoundsException("The number of row names does not correspond with the size of this matrix");
            this.rowNames = Arrays.copyOf(rowNames, rows);
        }
        return this;
    }

    /**
     * Set an entire row
     *
     * @param index   the row index (0-based)
     * @param rowData the data
     * @return this object for a fluent interface
     */
    public PrintableMatrix<C, R> setRow(int index, @NotNull double[] rowData) {
        if (rowData.length != columns)
            throw new ArrayIndexOutOfBoundsException("The number of elements does not correspond with the size of this matrix");
        if (index < 0 || index >= rows)
            throw new ArrayIndexOutOfBoundsException("Index is outside of the matrix rows");
        this.data[index] = Arrays.copyOf(rowData, columns);
        return this;
    }

    /**
     * Set an entire column
     *
     * @param index   the column index (0-based)
     * @param colData the data
     * @return this object for a fluent interface
     */
    public PrintableMatrix<C, R> setColumn(int index, @NotNull double[] colData) {
        if (colData.length != rows)
            throw new ArrayIndexOutOfBoundsException("The number of elements does not correspond with the size of this matrix");
        if (index < 0 || index >= columns)
            throw new ArrayIndexOutOfBoundsException("Index is outside of the matrix columns");
        for (int i = 0; i < rows; i++) {
            this.data[i][index] = colData[i];
        }
        return this;
    }

    /**
     * Print this matrix
     *
     * @param ps            a print stream
     * @param separator     the output separator
     * @param convertData   a nullable function that converts an element to a string
     * @param convertColumn a nullable function that converts a column name to a string
     * @param convertRow    a nullable function that converts a row name to a string
     */
    public void print(PrintStream ps,
                      String separator,
                      @Nullable DoubleFunction<String> convertData,
                      @Nullable Function<C, String> convertColumn,
                      @Nullable Function<R, String> convertRow) {
        if (convertData == null) convertData = Double::toString;
        if (convertColumn == null) convertColumn = Objects::toString;
        if (convertRow == null) convertRow = Objects::toString;
        if (columnNames != null) {
            ps.println(Arrays.stream(columnNames).map(convertColumn).collect(Collectors.joining(separator)));
        }
        for (var i = 0; i < rows; i++) {
            if (rowNames != null) {
                ps.print(convertRow.apply(rowNames[i]));
                ps.print(separator);
            }
            ps.println(Arrays.stream(data[i]).mapToObj(convertData).collect(Collectors.joining(separator)));
        }
    }

}
