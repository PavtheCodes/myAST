package org.ptcc.internals.Rules.EJClasses;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Templates.AbstractClassRule;
import org.ptcc.internals.Collections.Violation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.*;
import java.util.stream.IntStream;

class EJRule16 extends AbstractClassRule {

    @Override
    public void analyze(Node node, List<Violation> violations) {
        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
        reflectionAnnotation(clazz);
        if (!clazz.isPublic()) {
            violations.add(new Violation.Builder("Class is a nested class", Severity.INFO).build());
        }

        List<FieldDeclaration> fields = clazz.findAll(FieldDeclaration.class);
        if(fields.isEmpty()) { violations.add(new Violation.Builder("No fields found.", Severity.INFO).build()); }
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
                        ).lineNum(line).build());

                    }
                    else if (f.isPublic()) {
                        // public non final -> more severe
                        String fieldName = f.getVariables().get(0).getNameAsString();
                        int line = f.getRange().map(r -> r.begin.line).orElse(-1);
                        int column = f.getRange().map(r -> r.begin.column).orElse(-1);
                        violations.add(new Violation.Builder(
                                "Public field '" + fieldName + "' should use accessor methods. Line: " + line + ", Column: " + column,
                                Severity.HIGH
                        ).lineNum(line).build());
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

    // This one was really hard to figure out. Parsing annotations within annotations is harder than expected lol.
    public void reflectionAnnotation(Node node) {
        if (node instanceof ClassOrInterfaceDeclaration) {
            List<AnnotationExpr> stmt = node.findAll(AnnotationExpr.class);
        }
    }
}
// Process to parse java annotations idea: Parse the whole import declaration and check for java.lang.annotation.*;

/**
 * Lessons learned with this Rule:
 *      - findAll() is recursive and ignores early return statements.
 *          Even if I wanted to ignore fields from an inner class, returning the inner class empty means nothing
 *
 *
 * Todo List:
 *  - Parsing Reflection. In Lombok, fields are required to be Public, therefore should be avoided.
 *  - Accounting for fields that have several declarations, for example: "String test, test2, test3".
 **/