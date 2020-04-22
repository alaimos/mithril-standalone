package com.alaimos.MITHrIL.CommandLine.Threaded;

import org.jetbrains.annotations.NotNull;

public class ProcessingThreadGroup extends ThreadGroup {

    public ProcessingThreadGroup() {
        super("Processing-Threads");
    }

    @Override
    public void uncaughtException(@NotNull Thread t, @NotNull Throwable e) {
        System.err.print("Exception in processing thread \"" + t.getName() + "\" ");
        e.printStackTrace(System.err);
        System.exit(110);
    }
}
