package org.ptcc.internals.Rules.EJClasses;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Templates.AbstractClassRule;
import org.ptcc.internals.Collections.Violation;

import java.util.*;

/**
 * Effective Java rule focused on encapsulation and field exposure.
 *
 * This rule flags classes that expose public fields, with lower severity for
 * public final fields and higher severity for mutable public fields. It also
 * records empty classes and nested class context for those edge cases.
 */
class FieldExposureRule extends AbstractClassRule {

    @Override
    public void analyze(Node node, List<Violation> violations) {
        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
        reflectionAnnotation(clazz);
        if (!clazz.isPublic()) {
            violations.add(new Violation.Builder("Class is a nested class", Severity.INFO).at(clazz).build());
        }

        List<FieldDeclaration> fields = clazz.findAll(FieldDeclaration.class);
        if(fields.isEmpty()) { violations.add(new Violation.Builder("No fields found.", Severity.INFO).at(clazz).build()); }
        fields.stream()
                .filter(field -> !isFieldInNestedClass(field, clazz))
                .forEach(f -> {
                    if (f.isPublic() && f.isFinal()) {
                        // public final -> less severe
                        String fieldName = f.getVariables().get(0).getNameAsString();
                        int line = f.getRange().map(r -> r.begin.line).orElse(-1);
                        int column = f.getRange().map(r -> r.begin.column).orElse(-1);
                        violations.add(new Violation.Builder(
                                "Public final field '" + fieldName + "' - consider accessor methods. Line: " + line + ", Column: " + column,
                                Severity.LOW
                        ).at(f).build());

                    }
                    else if (f.isPublic()) {
                        // public non final -> more severe
                        String fieldName = f.getVariables().get(0).getNameAsString();
                        int line = f.getRange().map(r -> r.begin.line).orElse(-1);
                        int column = f.getRange().map(r -> r.begin.column).orElse(-1);
                        violations.add(new Violation.Builder(
                                "Public field '" + fieldName + "' should use accessor methods. Line: " + line + ", Column: " + column,
                                Severity.HIGH
                        ).at(f).build());
                    }

                });
    }

    public boolean isFieldInNestedClass(FieldDeclaration field, ClassOrInterfaceDeclaration topLevelClass) {
        return field.getParentNode().isPresent() &&
                field.getParentNode().get() != topLevelClass; // Checks if field is in the main node, else false;
    }

    @Deprecated
    public boolean isNestedClass(Node node) {
        if (node instanceof ClassOrInterfaceDeclaration) {
            return node.getParentNode().isPresent()
                    &&
                    node.getParentNode().get() instanceof ClassOrInterfaceDeclaration;
        }
        return false;
    }

    public void reflectionAnnotation(Node node) {
        if (node instanceof ClassOrInterfaceDeclaration) {
            List<AnnotationExpr> stmt = node.findAll(AnnotationExpr.class);
        }
    }
}
