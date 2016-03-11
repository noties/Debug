package ru.noties.debug.apt;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import ru.noties.debug.apt.annotations.DebugConfiguration;

/**
 * Created by Dimitry Ivanov on 10.03.2016.
 */
public class DebugProcessor extends AbstractProcessor {

    private ProcessingEnvironment mProcessingEnvironment;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("ru.noties.debug.apt.annotations.DebugConfiguration");
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

                // todo, change
                if (elements.size() > 1) {
                    throw new RuntimeException("Multiple @DebugConfiguration annotations");
                }

                final TypeElement typeElement = (TypeElement) elements.iterator().next();
                final DebugConfiguration debugConfiguration = typeElement.getAnnotation(DebugConfiguration.class);
                final String[] removeLabels = debugConfiguration.removeLabels();
                if (removeLabels != null
                        && removeLabels.length > 0) {
                    final Set<? extends Element> root = roundEnv.getRootElements();
                    final DebugTreeModifier treeModifier = DebugTreeModifier.newInstance(mProcessingEnvironment);
                    treeModifier.modify(removeLabels, root);
                }
            }
        }

        return false;
    }
}
