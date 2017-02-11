package ru.noties.debug.out;

import java.util.Collection;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
public class DebugOutputFacade implements DebugOutput {

    public static DebugOutputFacade newInstance(DebugOutput... outputs) {
        return new DebugOutputFacade(outputs);
    }

    public static DebugOutputFacade newInstance(Collection<? extends DebugOutput> collection) {
        final DebugOutput[] outputs = new DebugOutput[collection.size()];
        collection.toArray(outputs);
        return new DebugOutputFacade(outputs);
    }

    private final DebugOutput[] outputs;

    private DebugOutputFacade(DebugOutput[] outputs) {
        this.outputs = outputs;
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = outputs.length; i < size; i++) {
            if (outputs[i].isDebug()) {
                outputs[i].log(level, throwable, tag, message);
            }
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
