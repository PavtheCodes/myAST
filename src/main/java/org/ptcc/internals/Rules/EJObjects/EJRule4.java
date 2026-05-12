package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.List;

public class EJRule4 implements Rule {
    @Override
    public void check(Node node, List<Violation> violations) throws IOException {
        if (!(node instanceof ClassOrInterfaceDeclaration clazz)) {
            return;
        }

        List<MethodDeclaration> clazzMethods = clazz.getMethods();
        List<FieldDeclaration> clazzFields = clazz.getFields();
        List<ConstructorDeclaration> clazzConstructors = clazz.getConstructors();

        if (allMethodAndFieldStaticCheck(clazzMethods, clazzFields) && hasInstantiatingConstructor(clazzConstructors)) {
            violations.add(new Violation.Builder(
                    "Potential utility class detected, constructor must be private to ensure non-instantiability",
                    Severity.WARNING)
                    .build());
        }
    }
    public boolean allMethodAndFieldStaticCheck(List<MethodDeclaration> methodList, List<FieldDeclaration> fieldList) {
        if (methodList.isEmpty() && fieldList.isEmpty()) {
            return false;
        }
        for(MethodDeclaration method : methodList) {
            if(!method.isStatic()) {
                return false;
            }
        }
        for(FieldDeclaration field : fieldList) {
            if(!field.isStatic()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasInstantiatingConstructor(List<ConstructorDeclaration> constructorList) {
        if(constructorList.isEmpty()) {
            return true;
        }
        return constructorList.stream().anyMatch(constructor -> !constructor.isPrivate());
    }

}

