package ru.noties.debug.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Random;

import ru.noties.debug.Debug;
import ru.noties.debug.Timer;
import ru.noties.debug.TimerType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, new MainFragment())
                    .commit();
        }

        // Trace current method calls chain
        Debug.trace(100);

        someMethod(1, 5, "Hello!");

        someMethodWithException();

        simpleMethod();

        methodWithTimer();

        Debug.e("ok");

        methodWithTimerNano();

        throwException();

        objectPrint();

        labels();
    }

    private void someMethod(int x, int x2, String y) {
        Debug.i("x: %d, x2: %d, y: %s", x, x2, y);
    }

    private void someMethodWithException() {
        try {
            throw new AssertionError("This is exception");
        } catch (Throwable throwable) {
            Debug.e(throwable);
        }
    }

    private void simpleMethod() {
        Debug.w();
    }

    private void methodWithTimer() {
        doTiming(Debug.newTimer("Timer #1"));
    }

    private void methodWithTimerNano() {
        doTiming(Debug.newTimer("Timer Nano #2", TimerType.NANO));
    }

    private void doTiming(final Timer timer) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final Random random = new Random();
                timer.start("here we go, someVar: %d", 10);

                for (int i = 0; i < 22; i++) {
                    if ((i & 1) == 0) {
                        timer.tick();
                    } else {
                        timer.tick("i: %d", i);
                    }

                    // Dont do it. Ever
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        Debug.e(e);
                    }
                }

                timer.stop();
                Debug.i(timer.toString());
            }
        }).start();
    }

    private void throwException() {
//        throw new IllegalStateException("Testing uncaught exception");
    }

    private void objectPrint() {
        Debug.i(1);
        Debug.d(1);
        Debug.v(1);
        Debug.w(1);
        Debug.e(1);
        Debug.wtf(1);
    }

    private void labels() {
        root: {
            debug: Toast.makeText(this, "debug", Toast.LENGTH_LONG).show();
            release: Toast.makeText(this, "release", Toast.LENGTH_LONG).show();
        }
    }
}
