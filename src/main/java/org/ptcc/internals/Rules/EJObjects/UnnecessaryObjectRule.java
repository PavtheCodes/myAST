package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.List;
import java.util.Set;

/**
 * Detects unnecessary object creation for wrapper types and String.
 *
 * Examples:
 * - {new Integer(5)} should usually be {int}
 * - {new Boolean(false)} should usually be {boolean}
 * - {new String("text")} should usually be a string literal
 */
class UnnecessaryObjectRule implements Rule {
    private static final Set<String> UNNECESSARY_OBJECT_TYPES = Set.of(
            "Boolean",
            "Byte",
            "Character",
            "Double",
            "Float",
            "Integer",
            "Long",
            "Short",
            "String"
    );

    @Override
    public void check(Node node, List<Violation> violations) {
        node.findAll(ObjectCreationExpr.class).stream()
                .filter(this::isUnnecessaryObjectCreation)
                .forEach(expr -> violations.add(new Violation.Builder(
                        "Unnecessary object creation for " + expr.getType().asString()
                                + ". Use the primitive or literal form instead.",
                        Severity.WARNING).at(expr).build()));
    }

    private boolean isUnnecessaryObjectCreation(ObjectCreationExpr expr) {
        return UNNECESSARY_OBJECT_TYPES.contains(expr.getType().getNameAsString());
    }
}
