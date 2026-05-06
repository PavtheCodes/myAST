package org.ptcc.internals.Rules.EJGeneral.NamingConventionRule;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.SimpleName;
import org.ptcc.internals.Collections.NodeTypes;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.*;
import java.util.regex.Pattern;



// Adhere to naming conventions
public class EJRule68 implements Rule {
    private enum CaseConvention {
        CAMEL_CASE("^[a-z][a-zA-Z0-9]*$"),
        PASCAL_CASE("^[A-Z][a-zA-Z0-9]*$"),
        UPPER_SNAKE("^[A-Z][A-Z0-9_]*$"),
        SNAKE_CASE("^[a-z][a-z0-9_]*$");
        private final Pattern pattern;
        public final int counter;

        CaseConvention(String regex) {
            this.pattern = Pattern.compile(regex);
            this.counter = 0;
        }

        public boolean matches(String name) {
            return pattern.matcher(name).matches();
        }
        static CaseConvention detectConvention(String name) {
            if (PASCAL_CASE.matches(name)) return PASCAL_CASE;
            if (CAMEL_CASE.matches(name)) return CAMEL_CASE;
            if (UPPER_SNAKE.matches(name)) return UPPER_SNAKE;
            if (SNAKE_CASE.matches(name)) return SNAKE_CASE;
            return null;
        }
    }

    Map<Class<? extends Node>, Map<CaseConvention, Integer>> counters = new HashMap<>();

    public EJRule68() {
        for(NodeTypes n : NodeTypes.values()) {
            counters.put(n.getNodeClass(), new HashMap<>());
        }
    }

    /**
     * Analyzes a Java source file for naming convention consistency across the programs declarations.
     * This method performs a two-pass analysis on the provided {@link CompilationUnit}:
     * <ul>
     *     <li>Pass 1: Collects naming convention statistics for classes, methods,
     *     fields, variables, parameters, and enum constants.</li>
     *     <li>Pass 2: Compares each declaration's naming convention against the
     *     most frequently used convention for its category and records violations
     *     when mismatches are found.</li>
     * </ul>
     *
     * The analysis assumes the input node is a {@link CompilationUnit}.
     * Nodes that do not belong to supported declaration types are ignored.
     * Violations are appended to the violations list and represent deviations from
     * the dominant naming convention within the analyzed CompilationUnit.
     *
     * @param node the root {@link CompilationUnit} representing a Java source file
     * @param violations a mutable list where detected naming violations will be added
     */
    @Override
    public void check(Node node, List<Violation> violations) {
        if (!(node instanceof CompilationUnit cu)) return;

        // PASS 1
        cu.findAll(SimpleName.class)
                .forEach(this::incrementConvention);

        // PASS 2
        cu.findAll(SimpleName.class)
                .forEach(n -> secondPass(n, violations));
    }
    /**
     * Evaluates a single name node against the dominant naming convention
     * for its declaration type.
     * <p>
     * This method determines the parent node of the given {@link SimpleName},
     * detects its naming convention, and compares it to the most frequently
     * used convention for that node type.
     * <p>
     * If the name does not match the dominant convention, a violation is recorded.
     *
     * @param nameNode the name node being evaluated
     * @param violations list of violations to append to when inconsistencies are found
     */
    private void secondPass(SimpleName nameNode, List<Violation> violations) {
        Node parent = nameNode.getParentNode().orElse(null);
        if (parent == null) return;

        String name = nameNode.asString();
        CaseConvention detected = CaseConvention.detectConvention(name);
        if (detected == null) return;

        if (parent instanceof ClassOrInterfaceDeclaration) {
            checkAgainstMajority(ClassOrInterfaceDeclaration.class, detected, violations, "Classes");
        }
        else if (parent instanceof MethodDeclaration) {
            checkAgainstMajority(MethodDeclaration.class, detected, violations, "Methods");
        }
        else if (parent instanceof FieldDeclaration) {
            checkAgainstMajority(FieldDeclaration.class, detected, violations, "Fields");
        }
        else if (parent instanceof EnumConstantDeclaration) {
            checkAgainstMajority(EnumConstantDeclaration.class, detected, violations, "Enums");
        }
        else if (parent instanceof Parameter) {
            checkAgainstMajority(Parameter.class, detected, violations, "Parameters");
        }
        else if (parent instanceof VariableDeclarator) {
            checkAgainstMajority(VariableDeclarator.class, detected, violations, "Variables");
        }
    }
    /**
     * Collects naming convention statistics from a single {@link SimpleName} node.
     * <p>
     * This method identifies the declaration type of the given name and forwards
     * it to {@link #incrementDetectedConvention(Class, String)} for classification.
     * <p>
     * Only supported declaration types are processed.
     * Unsupported or unrelated nodes are ignored.
     *
     * @param nameNode the name node used to update convention frequency statistics
     */
    private void incrementConvention(SimpleName nameNode) {
        Node parent = nameNode.getParentNode().orElse(null);
        if (parent == null) return;

        String name = nameNode.asString();

        if (parent instanceof ClassOrInterfaceDeclaration) {
            incrementDetectedConvention(ClassOrInterfaceDeclaration.class, name);
        }
        else if (parent instanceof MethodDeclaration) {
            incrementDetectedConvention(MethodDeclaration.class, name);
        }
        else if (parent instanceof FieldDeclaration) {
            incrementDetectedConvention(FieldDeclaration.class, name);
        }
        else if (parent instanceof EnumConstantDeclaration) {
            incrementDetectedConvention(EnumConstantDeclaration.class, name);
        }
        else if (parent instanceof Parameter) {
            incrementDetectedConvention(Parameter.class, name);
        }
        else if (parent instanceof VariableDeclarator) {
            incrementDetectedConvention(VariableDeclarator.class, name);
        }
    }
    /**
     * Updates internal counters for naming convention frequency.
     * <p>
     * If the given name matches a known naming convention, the corresponding
     * counter for the specified declaration type is incremented.
     * <p>
     * This data is later used to determine the most commonly used naming style
     * per declaration category in order to check frequency.
     *
     * @param type the AST node type representing the declaration category
     * @param name the identifier whose naming convention is being recorded
     */
    private void incrementDetectedConvention(Class<? extends Node> type, String name) {
        CaseConvention detected = CaseConvention.detectConvention(name);


        if (detected == null) return;

        Map<CaseConvention, Integer> innerMap =
                counters.computeIfAbsent(type, k -> new HashMap<>());

        innerMap.merge(detected, 1, Integer::sum);
    }
    /**
     * Determines the most frequently used naming convention for a given node type.
     * <p>
     * This method scans the stored convention counters for the specified
     * declaration category and returns the convention with the highest frequency.
     * <p>
     * If no conventions have been recorded for the given type, {@code null} is returned.
     *
     * @param node the AST node type representing the declaration category
     * @return a map entry containing the most used convention and its count,
     *         or {@code null} if no data exists
     */
    public Map.Entry<CaseConvention, Integer> mostUsedNamingCondition(Class<? extends Node> node) {
        Map<CaseConvention, Integer> innerMapOfNode =
                counters.computeIfAbsent(node, defaultMap -> new HashMap<>());

        CaseConvention mostUsedConvention = null;
        int maxUsedCounter = -1;

        for (Map.Entry<CaseConvention, Integer> entry : innerMapOfNode.entrySet()) {
            int value = entry.getValue();

            if (value > maxUsedCounter) {
                maxUsedCounter = value;
                mostUsedConvention = entry.getKey();
            }
        }

        return mostUsedConvention == null ? null : Map.entry(mostUsedConvention, maxUsedCounter);
    }
    /**
     * Compares a detected naming convention against the dominant convention
     * for a given declaration type.
     * <p>
     * If the detected convention differs from the most frequently used convention,
     * a {@link Violation} is added to the provided list.
     * <p>
     * This method enforces consistency within a file by aligning naming usage
     * to the statistically dominant style rather than a fixed rule set.
     *
     * @param type the AST node type being evaluated
     * @param detected the naming convention detected for the current identifier
     * @param violations list where violations are recorded
     * @param label human-readable category name used in violation messages
     */
    private void checkAgainstMajority(
            Class<? extends Node> type,
            CaseConvention detected,
            List<Violation> violations,
            String label
    ) {
        Map.Entry<CaseConvention, Integer> mostUsed = mostUsedNamingCondition(type);

        if (mostUsed == null || mostUsed.getKey() == null) return;

        if (!detected.equals(mostUsed.getKey())) {
            violations.add(new Violation.Builder(
                    label + " should follow " + mostUsed.getKey(),
                    Severity.WARNING
            ).build());
        }
    }
}
