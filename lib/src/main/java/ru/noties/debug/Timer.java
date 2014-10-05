package ru.noties.debug;

/**
 * Created by dimaster on 05.10.14.
 */
public interface Timer {

    void tick   ();
    void start  ();
    void stop   ();

    void tick   (String message, Object... args);
    void start  (String message, Object... args);
    void stop   (String message, Object... args);

}
