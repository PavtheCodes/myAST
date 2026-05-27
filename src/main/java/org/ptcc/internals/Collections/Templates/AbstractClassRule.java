package org.ptcc.internals.Collections.Templates;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.List;

public abstract class AbstractClassRule implements Rule {
    public abstract void analyze(Node node, List<Violation> violations);

    @Override
    public final void check(Node node, List<Violation> violations) {
        if (!(node instanceof ClassOrInterfaceDeclaration clazz)) {
            return;
        }
        if (clazz.isInterface()) {
            return;
        }

        if (clazz.isEmpty()) {
            violations.add(new Violation.Builder("Class is empty", Severity.INFO).at(clazz).build());
        }

        analyze(node, violations);
    }
}
