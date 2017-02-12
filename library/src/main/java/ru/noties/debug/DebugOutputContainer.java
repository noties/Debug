package ru.noties.debug;

@SuppressWarnings("WeakerAccess")
public class DebugOutputContainer implements DebugOutput {

    private final DebugOutput[] outputs;

    public DebugOutputContainer(DebugOutput[] outputs) {
        this.outputs = outputs != null
                ? outputs
                : new DebugOutput[0];
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        for (DebugOutput output: outputs) {
            if (output != null && output.isDebug()) {
                output.log(level, throwable, tag, message);
            }
        }
    }

    @Override
    public boolean isDebug() {
        for (DebugOutput output: outputs) {
            if (output != null && output.isDebug()) {
                return true;
            }
        }
        return false;
    }
}
