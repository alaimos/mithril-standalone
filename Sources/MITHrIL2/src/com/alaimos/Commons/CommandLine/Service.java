package com.alaimos.Commons.CommandLine;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public interface Service extends Runnable {

    String getShortName();

    String getDescription();

    Options getOptions();

}
