package org.ptcc.internals.Rules.EJClasses;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.List;

public class StaticInnerClassRule implements Rule {
    @Override
    public void check(Node node, List<Violation> violations) throws IOException {
        if (!(node instanceof CompilationUnit cu)) {
            return;
        }

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
            boolean isInnerClass = clazz.getParentNode().isPresent() &&
                    clazz.getParentNode().get() instanceof ClassOrInterfaceDeclaration;

            if (isInnerClass && !clazz.isStatic()) {
                violations.add(new Violation.Builder(
                        "Inner class '" + clazz.getNameAsString() +
                                "' should be static if it doesn't reference outer class instance",
                        Severity.SUGGESTION
                ).at(clazz).build());
            }
        });
    }
}
