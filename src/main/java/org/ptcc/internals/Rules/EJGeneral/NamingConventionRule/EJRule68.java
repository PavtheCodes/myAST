package org.ptcc.internals.Rules.EJGeneral.NamingConventionRule;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import org.ptcc.internals.Collections.NodeTypes;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.ptcc.internals.Rules.EJGeneral.NamingConventionRule.NamingConventions.CAMEL_CASE;
import static org.ptcc.internals.Rules.EJGeneral.NamingConventionRule.NamingConventions.PASCAL_CASE;

// Adhere to naming conventions
public class EJRule68 implements Rule {

    Map<Class<? extends Node>, Map<NamingConventions, Integer>> counters = new HashMap<>();

    public EJRule68() {
        for(NodeTypes n : NodeTypes.values()) {
            counters.put(n.getNodeClass(), new HashMap<>());
        }
    }

    /**
     *
     *
     * @param node This node, has to be the CompilationUnit node.
     * @param violations
     * @throws IOException
     */
    @Override
    public void check(Node node, List<Violation> violations) throws IOException {
        if (!(node instanceof CompilationUnit)) return;
        List<Class<? extends Node>> types =
                Arrays.stream(NodeTypes.values())
                        .map(NodeTypes::getNodeClass)
                        .collect(Collectors.toList());

        node.findAll(SimpleName.class).stream()
                /*
                 *   We're running .map on the getParentNode() method, meaning.. if a parent node exists
                 *   then we check if anyMatch with the types list, where we check if type is an instance of
                 *   the node we passed in (parent).
                 *   tldr; parent instanceof type;
                 *   note: isInstance is the reflection equivalent of "instanceof"
                 */
                .filter(n -> n.getParentNode()
                        .map(parent -> types.stream().anyMatch(type -> type.isInstance(parent)))
                        .orElse(false))
                .forEach(f -> {
                    System.out.println(f + " belongs to: " + f.getParentNode().get());
                    patternFinder(f, violations);
                });

    }
    public void patternFinder(SimpleName nameNode, List<Violation> violations) {
        /*  Check for Class / Interface Names
        *   detectedConvention will check if Class name is in pascal case and increment the Class hashmap inner counter
        *   ** If the detected convention is anything but Pascal, a warning violation is thrown.
         */
        if(nameNode.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
            NamingConventions detected = NamingConventions.detectConvention(nameNode.asString()); // Get the Convention of nameNode
            Map<NamingConventions, Integer> inner = counters.computeIfAbsent(
                    ClassOrInterfaceDeclaration.class,                  // This will "bind" inner to the counter where ClassDeclaration is the key
                    k -> new HashMap<>()                                // Checks for a hashmap that might exist, otherwise creates one.
            );
            if(PASCAL_CASE.matches(nameNode.asString())) {
                inner.merge(PASCAL_CASE, 1, Integer::sum);  // Could be a one-liner, but I'm dumb.
            }
            else {
                inner.merge(detected, 1, Integer::sum);
                violations.add(new Violation.Builder("Pascal case is the recommended naming convention for classes", Severity.WARNING).build());
            }
        }
        else if(nameNode.getParentNode().get() instanceof MethodDeclaration) {
            NamingConventions detected = NamingConventions.detectConvention(nameNode.asString());
            Map<NamingConventions, Integer> inner = counters.computeIfAbsent(
                    MethodDeclaration.class,
                    k -> new HashMap<>()
            );
            if(CAMEL_CASE.matches(nameNode.asString())) {
                inner.merge(CAMEL_CASE, 1, Integer::sum);
            }
            else {
                inner.merge(detected, 1, Integer::sum);
                violations.add(new Violation.Builder("Camel case is the recommended naming convention for methods", Severity.WARNING).build());
            }
        }
    }
}

/* Optimal Way to have many filters, is to simply just write another one
*       .filter([Predicate])
*       .filter([Predicate])
*
*   - Learned how map.merge works, essentially checks for duplicate keys and merges values.
*     Merge cares only for values, but runs regardless if there are any duplicates or not.
*
*   - "map.computeIfAbsent ensures a key has a value. If it doesn't, it creates one. If it does, it returns the existing one. "
*   -  Leaving an enum unassigned will return null, even if the instance is instantiated.
*
*   TODO LIST:
*       - Change logic when searching for the Node types, findAll(SimpleName) is faulty.
*         We wanna ignore annotations for example, but they're still parsed and accounted for!
 */