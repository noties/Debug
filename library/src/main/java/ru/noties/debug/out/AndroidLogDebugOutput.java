package ru.noties.debug.out;

import android.util.Log;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
public class AndroidLogDebugOutput implements DebugOutput {

    private final boolean isDebug;

    public AndroidLogDebugOutput(boolean isDebug) {
        this.isDebug = isDebug;
    }
    
    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        switch (level) {
            case V:
                Log.v(tag, message, throwable);
                break;
            case D:
                Log.d(tag, message, throwable);
                break;
            case I:
                Log.i(tag, message, throwable);
                break;
            case W:
                Log.w(tag, message, throwable);
                break;
            case E:
                Log.e(tag, message, throwable);
                break;
            case WTF:
                Log.wtf(tag, message, throwable);
                break;
        }
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }
}
