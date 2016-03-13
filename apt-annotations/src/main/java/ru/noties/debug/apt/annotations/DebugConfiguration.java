package ru.noties.debug.apt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define labels to be processed.
 * The format: `label|label2|label3` (single and empty strings are OK)
 *
 * Created by Dimitry Ivanov on 10.03.2016.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DebugConfiguration {
     String allLabels();
     String enabledLabels();
}
