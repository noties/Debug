package ru.noties.debug.remove;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import ru.noties.debug.DebugRemove;

public class DebugRemoveProcessor extends AbstractProcessor {

    private DebugTreeModifier mDebugTreeModifier;
    private Messager mMessager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.unmodifiableSet(Collections.singleton(DebugRemove.class.getName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            mDebugTreeModifier = DebugTreeModifier.newInstance(processingEnv);
        } catch (Throwable t) {
            throw new RuntimeException("Exception during obtaining proprietary " +
                    "`com.sun.tools.javac.processing.JavacProcessingEnvironment`. This " +
                    "processor requires it for Java AST modification", t);
        }

        mMessager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {

            final TypeElement element = annotations.iterator().next();
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(element);

            if (elements != null) {

                if (elements.size() > 1) {
                    for (Element e: elements) {
                        mMessager.printMessage(Diagnostic.Kind.NOTE, "@DebugRemove", e);
                    }
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Multiple @DebugRemove annotations (see above), terminating");
                    return false;
                }

                final TypeElement typeElement = (TypeElement) elements.iterator().next();
                final DebugRemove debugRemove = typeElement.getAnnotation(DebugRemove.class);
                if (debugRemove.value()) {
                    final long started = System.currentTimeMillis();
                    final int removed = mDebugTreeModifier.removeDebugCalls(roundEnv.getRootElements());
                    final long ended = System.currentTimeMillis();
                    mMessager.printMessage(
                            Diagnostic.Kind.NOTE,
                            String.format(Locale.US,"Debug-remove, removed calls: %d, processing took: %d ms", removed, (ended - started))
                    );
                }
            }
        }

        return false;
    }
}
