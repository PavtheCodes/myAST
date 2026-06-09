package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

import org.ptcc.internals.Config.AnalyzerConfig;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Effective Java (3rd Edition) - Item 2: Consider a builder when faced with many constructor parameters.
 *
 * This rule flags classes where any constructor has more than 4 parameters,
 * suggesting the Builder pattern as a more maintainable alternative.
 *
 * @see <a href="https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/">Effective Java by Joshua Bloch</a>
 */

class ConstructorParameterThresholdRule implements Rule {

    /**
     * Checks a parsed Java file for violations
     *
     * Evaluates all constructors in a class and flags issues based on the maximum parameter count:
     * - 5+ parameters: Low Severity violation, suggesting Builder pattern
     * - 4 parameters: Suggestion to consider structure as user approaches recommended limit
     *
     * @param node The AST node to check (expected to be a ClassOrInterfaceDeclaration)
     * @param violations List to add violations to, if any are found
     */
    @Override
    public void check(Node node, List<Violation> violations) {
        List<ConstructorDeclaration> classConstructors = new ArrayList<>();
        if(node instanceof ClassOrInterfaceDeclaration) {
            List<Node> classNodes = node.getChildNodes();
            for(Node n : classNodes) {
                if (n instanceof ConstructorDeclaration) {
                    classConstructors.add((ConstructorDeclaration) n);
                }
            }
            int max = parameterCount(classConstructors);
            int threshold = AnalyzerConfig.getInstance().getEjRule2ParameterThreshold();
            if(max > threshold + 6) {
                violations.add(new Violation.Builder("Far too many parameters.", Severity.HIGH).at(node).build());
            } else if(max > threshold + 1) {
                violations.add(new Violation.Builder("Strongly advised to use Builder Pattern", Severity.LOW).at(node).build());
            } else if(max >= threshold) {
                violations.add(new Violation.Builder("Constructor has \" + maxParams + \" parameters. Consider Builder pattern as parameters increase", Severity.SUGGESTION).at(node).build());
            }
            System.out.println(max);
        }
    }

    /***
     *
     * @param list List of all constructors parsed, usually by a class.
     * @return Returns all parameters of the largest constructor in the list as a new List.
     *
     */
    public int parameterCount(List<ConstructorDeclaration> list) {
        if(list.isEmpty()) {
            return 0;
        }
        ConstructorDeclaration highest = new ConstructorDeclaration();
        for(int i = 0, j = 0; i < list.size(); i++) {
            int temp = list.get(i).getParameters().size();
            if(temp > j) {
                j = temp;
                highest = list.get(i);
            }
        }
        return highest.getParameters().size();
    }
}