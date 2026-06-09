package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.List;

import static org.ptcc.internals.Collections.UtilClass.checkNodeForClass;

/**
 * Checks for classes that look like utility or factory containers but violate
 * the expected structure for those patterns.
 *
 * The rule reports classes with no methods, final classes that contain
 * non-static methods, and non-final classes that are being treated as utility
 * containers.
 */

class StaticFactoryMethods implements Rule {

    @Override
    public void check(Node node, List<Violation> violations) {
        if (!(node instanceof ClassOrInterfaceDeclaration clazz)) {
            return;
        }
        checkNodeForClass(node);
        if(clazz.getMethods().isEmpty()) {
            violations.add(new Violation.Builder("Class does not contain any methods.", Severity.INFO).at(clazz).build()); // CHANGE THIS, IF NO METHODS RETURN VIOLATION
        }
        if(clazz.isFinal()) {
            for(MethodDeclaration m : clazz.getMethods()) {
                if(!m.isStatic()) {
                    violations.add(new Violation.Builder("One or more methods are not Static.", Severity.WARNING).at(m).build());
                    break;
                }
            }
        }
        else {
            violations.add(new Violation.Builder("Class is not final, cannot be a Utility Class as they are Final.", Severity.INFO).at(clazz).build());
        }
    }
}

