package ru.noties.debug.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.net.UnknownHostException;

import ru.noties.debug.Debug;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Debug.v("onCreate here!");
        Debug.d(1, 2, 3, 4, 5);
        Debug.i("0", "1", "2", "3"); // will just enumerate strings
        Debug.w("%s %s %s", null, null, null); // will call String.format
        Debug.e(true, null, Integer.MAX_VALUE, Long.MIN_VALUE);
        Debug.wtf("No, really, WTF?!");

        Debug.i("array: %s", new int[]{1, 2, 3, 4, 5});

        // Trace current method calls chain
        Debug.trace(100);

        final UnknownHostException exception = new UnknownHostException();
        exception.initCause(new Throwable());
        Debug.e(exception, "Hello this is a message for exception");
        Debug.e(exception);

        Debug.i("object as json: %s", json(this));
    }

    // for example, postpone json parsing by creating a new temporary object
    // that overrides `toString`. Debug will call toString only after all logging
    // checks are completed
    @Nullable
    private static Object json(@Nullable Object in) {
        if (in == null) return null;
        return new Object() {
            @Override
            public String toString() {
                return "{\"fake-key\": \"fake-value\"}";
            }
        };
    }
}
