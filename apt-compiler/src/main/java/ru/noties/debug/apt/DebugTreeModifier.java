package ru.noties.debug.apt;

import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.Diagnostic;

import ru.noties.debug.apt.annotations.Label;

/**
 * Created by Dimitry Ivanov on 10.03.2016.
 */
public class DebugTreeModifier {

    public static DebugTreeModifier newInstance(ProcessingEnvironment environment) {
        final Context context = ((JavacProcessingEnvironment) environment).getContext();
        return new DebugTreeModifier(
                Trees.instance(environment),
                TreeMaker.instance(context),
                environment.getMessager()
        );
    }

    private static final String LABEL_ANNOTATION = Label.class.getName();

    private final Trees mTrees;
    private final TreeMaker mTreeMaker;
    private final Messager mMessager;

    DebugTreeModifier(Trees trees, TreeMaker treeMaker, Messager messager) {
        this.mTrees = trees;
        this.mTreeMaker = treeMaker;
        this.mMessager = messager;
    }

    void modify(String[] labels, Set<? extends Element> elements) {

        if (elements == null
                || elements.size() == 0) {
            return;
        }

        final DebugTreeTranslator modifier = new DebugTreeTranslator(labels, mTreeMaker, mMessager);
        for (Element element: elements) {
            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }
            final JCTree tree = (JCTree) mTrees.getTree(element);
            tree.accept(modifier);
        }
    }

    private static class DebugTreeTranslator extends TreeTranslator {

        private final String[] mLabels;
        private final int mLablesLength;
        private final Messager mMessager;
        private final JCTree.JCBlock mEmptyBlock;

//        private JCTree.JCClassDecl mJCClass;

        DebugTreeTranslator(String[] labels, TreeMaker treeMaker, Messager messager) {
            this.mLabels = labels;
            this.mLablesLength = labels.length;
            this.mMessager = messager;
            this.mEmptyBlock = treeMaker.Block(0, List.<JCTree.JCStatement>nil());
        }

        @Override
        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

            // lets try to iterate over class & remove labeled members first
            checkLabeledMembers(jcClassDecl);

            super.visitClassDef(jcClassDecl);
        }

        @Override
        public void visitLabelled(JCTree.JCLabeledStatement jcLabeledStatement) {
            super.visitLabelled(jcLabeledStatement);
            if (shouldRemoveLabel(jcLabeledStatement)) {
                jcLabeledStatement.body = mEmptyBlock;
            }
        }

        private void checkLabeledMembers(JCTree.JCClassDecl jcClassDecl) {

            final List<JCTree> members = jcClassDecl.getMembers();
            if (members == null
                    || members.size() == 0) {
                return;
            }

            List<JCTree> out = null;

            boolean append;

            Tree.Kind kind;

            for (JCTree tree: members) {

                append = true;

                kind = tree.getKind();

                if (Tree.Kind.METHOD == kind) {
                    if (shouldRemoveTree(((JCTree.JCMethodDecl) tree).getModifiers())) {
                        append = false;
                    }
                } else if (Tree.Kind.VARIABLE == kind) {
                    if (shouldRemoveTree(((JCTree.JCVariableDecl) tree).getModifiers())) {
                        append = false;
                    }
                } else if (Tree.Kind.CLASS == kind) {
                    if (shouldRemoveTree(((JCTree.JCClassDecl) tree).getModifiers())) {
                        append = false;
                    }
                }

                if (append) {
                    out = append(out, tree);
                }
            }

            if (out != null) {
                jcClassDecl.defs = out;
            }
        }

        private static <T> List<T> append(List<T> list, T value) {
            if (list == null) {
                return List.of(value);
            } else {
                return list.append(value);
            }
        }

        private static String extractLabel(JCTree.JCAnnotation annotation) {
            final List<JCTree.JCExpression> arguments = annotation.getArguments();
            if (arguments != null
                    && arguments.size() > 0) {
                final JCTree.JCAssign assign = (JCTree.JCAssign) arguments.get(0);
                return ((JCTree.JCLiteral) assign.rhs).getValue().toString();
            }
            return null;
        }

        private void log(Diagnostic.Kind kind, String pattern, Object... args) {
            mMessager.printMessage(kind, String.format(pattern, args));
        }

        private boolean shouldRemoveTree(JCTree.JCModifiers modifiers) {

            final List<JCTree.JCAnnotation> annotations = modifiers != null
                    ? modifiers.getAnnotations()
                    : null;

            if (annotations == null
                    || annotations.size() == 0) {
                return false;
            }

            Type type;

            for (JCTree.JCAnnotation annotation: annotations) {
                type = annotation.annotationType != null ? annotation.annotationType.type : null;
                if (type != null) {
                    if (LABEL_ANNOTATION.equals(type.toString())) {
                        if (shouldRemoveLabel(extractLabel(annotation))) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        private boolean shouldRemoveLabel(JCTree.JCLabeledStatement jcLabeledStatement) {
            return shouldRemoveLabel(jcLabeledStatement.label.toString());
        }

        private boolean shouldRemoveLabel(String label) {

            if (label == null
                    || label.length() == 0) {
                return false;
            }

            for (int i = 0, length = mLablesLength; i < length; i++) {
                if (mLabels[i].equals(label)) {
                    return true;
                }
            }
            return false;
        }
    }
}
