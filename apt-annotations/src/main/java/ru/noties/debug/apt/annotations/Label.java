package ru.noties.debug.apt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dimitry Ivanov on 24.05.2016.
 */
@Target({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE,
        ElementType.CONSTRUCTOR
})
@Retention(RetentionPolicy.SOURCE)
public @interface Label {
    String value();
}
