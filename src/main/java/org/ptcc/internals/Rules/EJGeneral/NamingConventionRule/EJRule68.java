package org.ptcc.internals.Rules.EJGeneral;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Templates.NamingConventionTemplates.NamingConventionRule;
import org.ptcc.internals.Collections.Violation;

import java.util.List;

// Adhere to naming conventions
public class EJRule68 extends NamingConventionRule {
    @Override
    public void analyze(Node node, List<Violation> violations) {
        if (!(node instanceof CompilationUnit)) {
            violations.add(new Violation.Builder("Node must be the whole root CompilationUnit", Severity.INFO).build());
        }
        List<Node> nodes = node.findAll(Node.class);

    }
}
