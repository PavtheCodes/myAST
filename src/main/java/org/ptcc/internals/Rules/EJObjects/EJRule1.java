package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.ArrayList;
import java.util.List;

import static org.ptcc.internals.Collections.UtilClass.checkNodeForClass;

class EJRule1 implements Rule {

    @Override
    public void check(Node node, List<Violation> violations) {
        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
        checkNodeForClass(node);
        if(clazz.getMethods().isEmpty()) {
            violations.add(new Violation.Builder("Class does not contain any methods.", Severity.INFO).build()); // CHANGE THIS, IF NO METHODS RETURN VIOLATION
        }
        if(clazz.isFinal()) {
            for(MethodDeclaration m : clazz.getMethods()) {
                if(!m.isStatic()) {
                    violations.add(new Violation.Builder("One or more methods are not Static.", Severity.WARNING).build());
                    break;
                }
            }

        }
        else {
            violations.add(new Violation.Builder("Class is not final, cannot be a Utility Class as they are Final.", Severity.INFO).build());
        }

    }
}

/* 1. Parse a Class
   2. Parse every method
   3. If all methods don't contain keyword "static"
   4. Parse All Constructors, Violation if more than one && Violation if Constructor isn't Private.
 */
