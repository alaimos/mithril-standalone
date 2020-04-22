package com.alaimos.Commons.Algorithm;

/**
 * Standard Exception thrown due to an error during the execution of an algorithmClass
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public class AlgorithmExecutionException extends RuntimeException {

    public AlgorithmExecutionException() {
        super();
    }

    public AlgorithmExecutionException(String message) {
        super(message);
    }

    public AlgorithmExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlgorithmExecutionException(Throwable cause) {
        super(cause);
    }

}
