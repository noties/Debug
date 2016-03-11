package ru.noties.debug.apt;

import com.sun.source.util.Trees;
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
        private final JCTree.JCStatement mEmptyBlock;

        DebugTreeTranslator(String[] labels, TreeMaker treeMaker, Messager messager) {
            this.mLabels = labels;
            this.mLablesLength = labels.length;
            this.mMessager = messager;
            this.mEmptyBlock = treeMaker.Block(0, List.<JCTree.JCStatement>nil());
        }

        @Override
        public void visitLabelled(JCTree.JCLabeledStatement jcLabeledStatement) {
            super.visitLabelled(jcLabeledStatement);
            if (shouldRemoveLabel(jcLabeledStatement)) {
                jcLabeledStatement.body = mEmptyBlock;
            }
        }

        private void log(Diagnostic.Kind kind, String pattern, Object... args) {
            mMessager.printMessage(kind, String.format(pattern, args));
        }

        private boolean shouldRemoveLabel(JCTree.JCLabeledStatement jcLabeledStatement) {
            final String label = jcLabeledStatement.label.toString();
            for (int i = 0, length = mLablesLength; i < length; i++) {
                if (mLabels[i].equals(label)) {
                    return true;
                }
            }
            return false;
        }
    }
}
