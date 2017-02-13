package ru.noties.debug.remove;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

class DebugTreeModifier {

    static DebugTreeModifier newInstance(ProcessingEnvironment environment) throws Throwable {
        return new DebugTreeModifier(Trees.instance(environment));
    }

    private static final String PACKAGE_NAME = "ru.noties.debug.";
    private static final int PACKAGE_NAME_LENGTH = PACKAGE_NAME.length();

    private final Trees mTrees;

    private DebugTreeModifier(Trees trees) {
        this.mTrees = trees;
    }

    // returns removed calls count
    int removeDebugCalls(Set<? extends Element> elements) {

        if (elements == null
                || elements.size() == 0) {
            return 0;
        }

        final RemoveDebugCallsTreeTranslator translator = new RemoveDebugCallsTreeTranslator();

        for (Element element: elements) {

            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }

            final JCTree tree = (JCTree) mTrees.getTree(element);
            tree.accept(translator);
        }

        return translator.mRemovedCalls;
    }

    private static class RemoveDebugCallsTreeTranslator extends TreeTranslator {

        int mRemovedCalls;

        @Override
        public void visitBlock(JCTree.JCBlock jcBlock) {
            super.visitBlock(jcBlock);

            final List<JCTree.JCStatement> statements = jcBlock.getStatements();

            if (statements != null
                    && statements.size() > 0) {

                List<JCTree.JCStatement> out = List.nil();

                JCTree.JCExpressionStatement expressionStatement;
                JCTree.JCMethodInvocation methodInvocation;

                for (JCTree.JCStatement statement: statements) {

                    if (statement instanceof JCTree.JCExpressionStatement) {
                        expressionStatement = (JCTree.JCExpressionStatement) statement;

                        if (expressionStatement.getExpression() instanceof JCTree.JCMethodInvocation) {
                            methodInvocation = (JCTree.JCMethodInvocation) expressionStatement.getExpression();

                            if (remove(methodInvocation.meth.toString())) {
                                mRemovedCalls += 1;
                                continue;
                            }
                        }
                    }
                    out = out.append(statement);
                }

                jcBlock.stats = out;
            }
        }

        private static boolean remove(String methodName) {

            // okay, there are 2 ways to call Debug
            // direct call (with import statement) - `Debug.*`
            // call specifying the full class name - `ru.noties.debug.Debug.*`

            // we could simply check if `methodName` endsWith our methods, for example
            // `methodName.endsWith("Debug.i");`, but it will match a lot more methods,
            // which will lead to great confusion, for example `AndroidDebug.i` will also be removed,
            // or some debug class from different package (weird, but it can be)...
            // so, if we can validate the full package -> do it, else put in README notice about
            // using the `Debug` class with different package, but that contain these methods

            // first, check the length
            // if length is the same -> no need to validate package
            // else, validate package

            final int length = methodName.length();

            return endsWith(methodName, length, "Debug.v")
                    || endsWith(methodName, length, "Debug.d")
                    || endsWith(methodName, length, "Debug.i")
                    || endsWith(methodName, length, "Debug.w")
                    || endsWith(methodName, length, "Debug.e")
                    || endsWith(methodName, length, "Debug.wtf")
                    || endsWith(methodName, length, "Debug.trace")
                    || endsWith(methodName, length, "Debug.init");
        }

        static boolean endsWith(String methodName, int methodNameLength, String text) {

            final boolean out;

            final int textLength = text.length();
            if (methodNameLength < textLength) {
                out = false;
            } else if (methodNameLength == textLength) {
                // validate direct equals
                out = methodName.equals(text);
            } else {
                // validate that it's our package
                if (methodName.startsWith(PACKAGE_NAME)) {
                    // so, we need to validate that left methodName is exact match
                    if (methodNameLength - PACKAGE_NAME_LENGTH == textLength) {
                        out = methodName.regionMatches(false, PACKAGE_NAME_LENGTH, text, 0, textLength);
                    } else {
                        out = false;
                    }
                } else {
                    out = false;
                }
            }

            return out;
        }
    }
}
