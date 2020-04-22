package com.alaimos.Commons.Observer.Interface;

import java.util.function.Consumer;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 25/12/2015
 */
@FunctionalInterface
public interface EventListener extends Consumer<EventInterface> {
}
