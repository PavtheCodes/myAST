package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.List;

public class EJRule4 implements Rule {

    @Override
    public void check(Node node, List<Violation> violations) {
        List<Node> methodNodes = node.getChildNodes().
        List<MethodDeclaration> methods = node.stream().allMatch(n -> n instanceof MethodDeclaration);
        if(node instanceof ClassOrInterfaceDeclaration) {

            node.findAll(MethodDeclaration.class).stream()

                    .filter(f -> f.getModifiers().contains(Modifier.staticModifier()))
                    .forEach(f -> {
                        System.out.println(f);
                    });
        }
    }
}

/* 1. Parse a Class
   2. Parse every method
   3. If all methods don't contain keyword "static"
   4. Parse All Constructors, Violation if more than one && Violation if Constructor isn't Private.
 */
