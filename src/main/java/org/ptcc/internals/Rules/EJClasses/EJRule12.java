package org.ptcc.internals.Rules.EJClasses;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


class EJRule12 implements Rule {
    /**
     * Analyzes a {@link CompilationUnit} to scan for toString violations, where if it's present then
     * it's completely ignored. If passed node is a class and does not have a toString
     * implementation, then a violation is thrown.
     *
     * @param node the root {@link CompilationUnit} representing a Java source file
     * @param violations a mutable list where detected naming violations will be added
     */
    @Override
    public void check(Node node, List<Violation> violations) throws IOException {
        Optional<ClassOrInterfaceDeclaration> classOptional = node.findFirst(ClassOrInterfaceDeclaration.class);

        if(classOptional.isPresent()) {
            ClassOrInterfaceDeclaration clazz = classOptional.get();
            boolean hasToString = false;
            for(MethodDeclaration method : clazz.getMethods()) {
                if(method.getNameAsString().equals("toString")) {
                    hasToString = true;
                    break;
                }
            }
            if (!hasToString) {
                violations.add(new Violation.Builder("Always override toString!", Severity.WARNING).build());
            }
        }
    }
}
