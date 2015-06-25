package ru.noties.debug.out;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
public class DebugOutputFacade implements DebugOutput {

    private final DebugOutput[] outputs;

    public DebugOutputFacade(DebugOutput... outputs) {
        this.outputs = outputs;
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = outputs.length; i < size; i++) {
            outputs[i].log(level, throwable, tag, message);
        }
    }

    @Override
    public boolean isDebug() {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = outputs.length; i < size; i++) {
            if (outputs[i].isDebug()) {
                return true;
            }
        }
        return false;
    }
}
