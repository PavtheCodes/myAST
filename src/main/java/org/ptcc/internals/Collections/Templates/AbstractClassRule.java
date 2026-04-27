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
    @Override // This override on check ensures that no matter what the user overrides the check method with, that this one runs first.
    public final void check(Node node, List<Violation> violations) {
        if(!(node instanceof ClassOrInterfaceDeclaration) || ((ClassOrInterfaceDeclaration) node).isInterface()) {
            violations.add(new Violation.Builder("Node is not a class.", Severity.INFO).build());
        }
        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
        if(clazz.isEmpty()) {
            violations.add(new Violation.Builder("Node is empty", Severity.INFO).build());
        }
        /*if(clazz.isNestedType()) {
            violations.add(new Violation.Builder("Node is nested, top level class required.", Severity.INFO).build());
        }*/
        analyze(node, violations);
    }
}
