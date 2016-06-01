package ru.noties.debug.apt;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import ru.noties.debug.apt.annotations.DebugConfiguration;

/**
 * Created by Dimitry Ivanov on 10.03.2016.
 */
public class DebugProcessor extends AbstractProcessor {

    private ProcessingEnvironment mProcessingEnvironment;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(DebugConfiguration.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mProcessingEnvironment = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {

            final TypeElement element = annotations.iterator().next();
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(element);

            if (elements != null) {

                if (elements.size() > 1) {
                    mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Multiple @DebugConfiguration annotations, terminating");
                    return false;
                }

                final long started = System.currentTimeMillis();

                final TypeElement typeElement = (TypeElement) elements.iterator().next();
                final DebugConfiguration debugConfiguration = typeElement.getAnnotation(DebugConfiguration.class);

                final String allLabelsString = debugConfiguration.allLabels();
                final String[] allLabels = parseInput(allLabelsString);
                if (allLabels != null && allLabels.length > 0) {

                    final String enabledLabelsStrings = debugConfiguration.enabledLabels();
                    final String[] enabledLabels = parseInput(enabledLabelsStrings);

                    final String[] removeLabels;
                    if (enabledLabels == null || enabledLabels.length == 0) {
                        removeLabels = allLabels;
                    } else {
                        // intersect
                        removeLabels = intersect(allLabels, enabledLabels);
                    }

                    if (removeLabels != null && removeLabels.length > 0) {
                        final Set<? extends Element> root = roundEnv.getRootElements();
                        final DebugTreeModifier treeModifier = DebugTreeModifier.newInstance(mProcessingEnvironment);
                        treeModifier.modify(removeLabels, root);
                    }
                }

                final long ended = System.currentTimeMillis();

                mProcessingEnvironment.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        String.format("Debug-apt, processing took: %d ms", (ended - started))
                );
            }
        }

        return false;
    }

    private static String[] parseInput(String in) {
        return in == null || in.length() == 0 ? null : in.split("\\|");
    }

    private static String[] intersect(String[] all, String[] enabled) {

        final Set<String> set = new HashSet<>(Arrays.asList(all));

        for (String label: enabled) {
            set.remove(label);
        }

        if (set.size() == 0) {
            return null;
        }

        return set.toArray(new String[set.size()]);
    }
}
