package ru.noties.debug.apt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dimitry Ivanov on 10.03.2016.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface DebugConfiguration {
     /**
      * Provide labels that must be removed from final build.
      * Must be in the format `label1|label2|label3`, single values &amp; empty string are OK
      * Please note, that these are the labels that WILL be removed
      */
     String removeLabels();
}
