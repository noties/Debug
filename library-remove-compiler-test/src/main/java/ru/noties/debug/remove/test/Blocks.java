package ru.noties.debug.remove.test;

import ru.noties.debug.Debug;

class Blocks {

    static {
        Debug.v("static block single line");
    }

    static {
        final String message = "static block multiple lines";
        Debug.d(message);
    }

    {
        Debug.i("instance init block, single line");
    }

    {
        final String message = "instance init block, multiple lines";
        Debug.w(message);
    }
}
