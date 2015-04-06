package ru.noties.debug;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 05.10.14.
 */
public interface Timer {

    void tick   ();
    void start  ();
    void stop   ();

    void tick   (String message, Object... args);
    void start  (String message, Object... args);
    void stop   (String message, Object... args);

}
