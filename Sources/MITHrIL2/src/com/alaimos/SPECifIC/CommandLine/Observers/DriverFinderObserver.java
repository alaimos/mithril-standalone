package com.alaimos.SPECifIC.CommandLine.Observers;

import com.alaimos.Commons.Observer.Interface.EventInterface;
import com.alaimos.Commons.Observer.ObserverImpl.Observer;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/01/2016
 */
public class DriverFinderObserver extends Observer {

    protected Consumer<String> print;

    public DriverFinderObserver(Consumer<String> print) {
        this.print = print;
        addEventListener("findingDriversPath", this::findingDriversPath);
    }

    private void findingDriversPath(@NotNull EventInterface e) {
        print.accept("..." + ((PathwayInterface) e.getData()).getId());
    }

}
