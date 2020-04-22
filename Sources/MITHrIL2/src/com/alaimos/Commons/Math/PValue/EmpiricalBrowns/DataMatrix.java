package com.alaimos.Commons.Math.PValue.EmpiricalBrowns;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 14/02/2017
 */
public class DataMatrix {

    private String[]   rowNames;
    private double[][] data;

    /**
     * Default data matrix constructor
     *
     * @param rowNames a vector of names for each row of the matrix
     * @param data     the data matrix as an array-of-arrays
     */
    public DataMatrix(String[] rowNames, double[][] data) {
        if (rowNames.length != data.length) throw new IllegalArgumentException("Number of rows do not match");
        this.rowNames = rowNames;
        this.data = data;
    }

    public int rowIndexByName(String row) {
        return ArrayUtils.indexOf(rowNames, row);
    }

    public int[] rowIndicesByNames(String[] rows) {
        int[] indexes = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            indexes[i] = rowIndexByName(rows[i]);
        }
        return indexes;
    }

    public String[] getRowNames() {
        return rowNames;
    }

    public double[][] getData() {
        return data;
    }

    public int rows() {
        return data.length;
    }

    public int cols() {
        return data[0].length;
    }

    public double[] getRow(int row) {
        if (row < 0 || row >= data.length) throw new OutOfRangeException(row, 0, data.length);
        return data[row].clone();
    }

    public double[] getRow(String row) {
        return getRow(rowIndexByName(row));
    }

    public double[][] getRows(int[] rows) {
        double[][] selection = new double[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            selection[i] = getRow(rows[i]);
        }
        return selection;
    }

    public double[][] getRows(String[] rows) {
        return getRows(rowIndicesByNames(rows));
    }

    /**
     * Get a subset of this matrix
     *
     * @param indexes a set of row index
     * @return a submatrix
     */
    public DataMatrix subMatrix(int[] indexes) {
        double[][] subData = getRows(indexes);
        String[] subRowNames = new String[indexes.length];
        for (int i = 0; i < subRowNames.length; i++) {
            subRowNames[i] = rowNames[indexes[i]];
        }
        return new DataMatrix(subRowNames, subData);
    }

    /**
     * Get a subset of this matrix
     *
     * @param rows a set of rows
     * @return a submatrix
     */
    public DataMatrix subMatrix(String[] rows) {
        return subMatrix(rowIndicesByNames(rows));
    }

    /**
     * Get the lower triangular part of this matrix
     *
     * @return the lower triangular matrix as an array
     */
    public double[] lowerTriangular() {
        return lowerTriangular(true);
    }

    /**
     * Get the lower triangular part of this matrix
     *
     * @param keepDiag keeps the diagonal of the matrix?
     * @return the lower triangular matrix as an array
     */
    public double[] lowerTriangular(boolean keepDiag) {
        ArrayList<Double> lower = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; (keepDiag) ? j <= i : j < i; j++) {
                lower.add(data[i][j]);
            }
        }
        return lower.stream().mapToDouble(v -> v).toArray();
    }

    /**
     * Compute the covariance matrix of this matrix
     *
     * @return the covariance matrix
     */
    public DataMatrix computeCovarianceMatrix() {
        RealMatrix mx = MatrixUtils.createRealMatrix(this.data);
        mx = mx.transpose();
        RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
        return new DataMatrix(this.rowNames.clone(), cov.transpose().getData());
    }

    /**
     * Apply a transformation to this matrix
     *
     * @param f a transformation
     */
    public void transform(Function<double[], double[]> f) {
        for (int i = 0; i < data.length; i++) {
            data[i] = f.apply(data[i]);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rowNames.length; i++) {
            sb.append(rowNames[i])
              .append(Arrays.toString(data[i]).replace('[', '\t').replace(']', '\n').replace(',', '\t'));
        }
        return sb.toString();
    }
}
