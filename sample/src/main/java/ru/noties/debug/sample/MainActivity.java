package ru.noties.debug.sample;

import android.app.Activity;
import android.os.Bundle;

import java.net.UnknownHostException;

import ru.noties.debug.Debug;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo, NOTICE that if enumeration of arguments is passed to the method
        // and first one is String -> there will be an exception, because Debug will try to call
        // String.format(args[0], args[1:-1]), so in order to skip internal String.format call pa

        // so, the idea is this -> if first argument is String -> try to String.format
        // if fails -> just enumerate, if success - it's it

        Debug.v("onCreate here!");
        Debug.d(1, 2, 3, 4, 5);
        Debug.i("0", "1", "2", "3"); // will just enumerate strings
        Debug.w("%s %s %s", null, null, null); // will call String.format
        Debug.e(true, null, Integer.MAX_VALUE, Long.MIN_VALUE);
        Debug.wtf("No, really, WTF?!");

//        // todo, `hello from a for loop` is not removed, must say it specifically...who would use something like that?
//        for (int i = 0; i < 10; i++, Debug.i("hello from a for loop")) {
//            Debug.i("body of a for loop");
//        }

        // Trace current method calls chain
        Debug.trace(100);

//        someMethod(1, 5, "Hello!");

//        someMethodWithException();

//        tracking: simpleMethod();

//        Debug.e("ok");

//        throwException();

//        objectPrint();

//        labels();

//        Debug.e(new UnknownHostException());

        final UnknownHostException exception = new UnknownHostException();
        exception.initCause(new Throwable());
        Debug.e(exception, "Hello this is a message for exception");
//
//        final String bigOne;
//        {
//            final int length = 8001;
//            final char[] chars = new char[length];
//            Arrays.fill(chars, 'c');
//            bigOne = new String(chars);
//            Debug.i(bigOne);
//        }
    }
//
//    private void someMethod(int x, int x2, String y) {
////        Debug.i("x: %d, x2: %d, y: %s", x, x2, y);
//        ru.noties.debug.Debug.i("hello from Debug.i()");
//
//    }

    private void someMethodWithException() {
        try {
            throw new AssertionError("This is exception");
        } catch (Throwable throwable) {
            Debug.e(throwable);
        }
    }

//    private void simpleMethod() {
//        Debug.w();
//    }

//    private void throwException() {
////        throw new IllegalStateException("Testing uncaught exception");
//    }
//
//    private void objectPrint() {
//        Debug.i(1);
//        Debug.d(1);
//        Debug.v(1);
//        Debug.w(1);
//        Debug.e(1);
//        Debug.wtf(1);
//    }

//    private void labels() {
//
//        debug: {
//            Debug.i("this thing will be here if `debug` label is set to be enabled");
//            Toast.makeText(this, "debug", Toast.LENGTH_SHORT).show();
//        }
//
//
//        // hhh
//        //noinspection UnusedLabel
//        tracking: {
//            // do some tracking, actual only for `release` build
//            Toast.makeText(this, "tracking", Toast.LENGTH_SHORT).show();
//        }
//
//        tracking: Toast.makeText(this, "one liner is also ok", Toast.LENGTH_SHORT).show();
//
//        $: {
//            Debug.i("text: %s", "some text");
//        }
//    }
}
