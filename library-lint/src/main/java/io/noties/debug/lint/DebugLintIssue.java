package io.noties.debug.lint;

import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebugLintIssue extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "DebugStringFormat",
            "String#format arguments mismatch",
            "Checks if Debug is called with correct pattern and arguments",
            Category.PERFORMANCE,
            10,
            Severity.WARNING,
            new Implementation(DebugLintIssue.class, Scope.JAVA_FILE_SCOPE));

    private static final Pattern STRING_FORMAT_PATTERN =
            Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");

    private static final Set<String> METHODS = new HashSet<>(Arrays.asList("v", "d", "i", "w", "e", "wtf"));

    @Nullable
    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        //noinspection unchecked
        return (List) Collections.singletonList(UCallExpression.class);
    }

    @Nullable
    @Override
    public UElementHandler createUastHandler(@NotNull final JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitCallExpression(@NotNull UCallExpression node) {

                final PsiMethod psiMethod = node.resolve();
                if (psiMethod != null
                        && context.getEvaluator().isMemberInClass(psiMethod, "io.noties.debug.Debug")) {

                    final String name = node.getMethodName();
                    if (name != null && METHODS.contains(name)) {
                        process(context, node);
                    }
                }
            }
        };
    }

    private static void process(@NonNull JavaContext context, @NonNull UCallExpression expression) {

        // to be able to mutate (we remove first Throwable if present)
        final List<UExpression> arguments = new ArrayList<>(expression.getValueArguments());
        if (arguments.isEmpty()) {
            // if there are no arguments -> no check
            return;
        }

        // remove throwable (comes first0
        if (isSubclassOf(context, arguments.get(0), Throwable.class)) {
            arguments.remove(0);
        }

        // still check for empty arguments (method can be called with just a throwable)
        // if first argument is not a string, then also nothing to do here
        if (arguments.isEmpty()
                || !isSubclassOf(context, arguments.get(0), String.class)) {
            return;
        }

        // now, first arg is string, check if it matches the pattern
        final String pattern = (String) arguments.get(0).evaluate();
        if (pattern == null
                || pattern.length() == 0) {
            // if no pattern is available -> return
            return;
        }

        final Matcher matcher = STRING_FORMAT_PATTERN.matcher(pattern);

        // we must _find_, not _matches_
        if (matcher.find()) {
            // okay, first argument is string
            // evaluate other arguments (actually create them)

            // remove pattern
            arguments.remove(0);

            // what else can we do -> count actual placeholders and arguments
            // (if mismatch... no, we can have positioned)
            final Object[] mock = mockArguments(arguments);

            try {
                //noinspection ResultOfMethodCallIgnored
                String.format(pattern, mock);
            } catch (Throwable t) {
                context.report(
                        ISSUE,
                        expression,
                        context.getLocation(expression),
                        t.getMessage());
            }
        }
    }

    @Nullable
    private static Object[] mockArguments(@NonNull List<UExpression> list) {

        if (list.isEmpty()) {
            return null;
        }

        final List<Object> objects = new ArrayList<>(list.size());

        for (UExpression expression : list) {
            final Object eval = expression.evaluate();
            if (eval != null) {
                objects.add(eval);
            } else {

                // we must really _mock_ it
                // check for primitives -> and create them, else just `new Object()`

                final Object o;

                final PsiType psiType = expression.getExpressionType();
                if (PsiType.BOOLEAN.equals(psiType)) {
                    o = false;
                } else if (PsiType.BYTE.equals(psiType)) {
                    o = (byte) 0;
                } else if (PsiType.CHAR.equals(psiType)) {
                    o = 'a';
                } else if (PsiType.DOUBLE.equals(psiType)) {
                    o = 0.0D;
                } else if (PsiType.FLOAT.equals(psiType)) {
                    o = 0.0F;
                } else if (PsiType.INT.equals(psiType)) {
                    o = 0;
                } else if (PsiType.LONG.equals(psiType)) {
                    o = 0L;
                } else if (PsiType.SHORT.equals(psiType)) {
                    o = (short) 0;
                } else if (PsiType.NULL.equals(psiType)) {
                    o = null;
                } else {
                    o = new Object();
                }

                objects.add(o);
            }
        }

        return objects.toArray();
    }

    private static boolean isSubclassOf(
            @NonNull JavaContext context,
            @NonNull UExpression expression,
            @NonNull Class<?> cls) {

        final PsiType expressionType = expression.getExpressionType();
        if (!(expressionType instanceof PsiClassType)) {
            return false;
        }

        final PsiClassType classType = (PsiClassType) expressionType;
        final PsiClass resolvedClass = classType.resolve();
        return context.getEvaluator().extendsClass(resolvedClass, cls.getName(), false);
    }
}
