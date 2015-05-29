package com.alaimos.Utils;

public interface Action<T> {

    public enum ActionResult {
        CONTINUE,
        PRUNE,
        STOP
    }

    public ActionResult run(T o);

}
