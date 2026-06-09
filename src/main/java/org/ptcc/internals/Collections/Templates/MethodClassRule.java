package org.ptcc.internals.Collections.Templates;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.List;

public abstract class MethodClassRule implements Rule {
    public abstract void analyze(Node node, List<Violation> violations);
    @Override
    public void check(Node node, List<Violation> violations) {
        if(!(node instanceof MethodDeclaration) || ((MethodDeclaration) node).isAbstract()) { // < Note the isAbstract
            violations.add(new Violation.Builder("Node is not a method.", Severity.INFO).at(node).build());
        }
        MethodDeclaration method = (MethodDeclaration) node;
        if(method.getBody().isEmpty()) {
            violations.add(new Violation.Builder("Method is empty", Severity.INFO).at(method).build());
        }
        analyze(node, violations);
    }
}
