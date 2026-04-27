package org.ptcc;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;
import org.ptcc.internals.Rules.EJGeneral.NamingConventionRule.EJRule68;
import org.ptcc.internals.Rules.ImplementationScanner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



public class Main {
    private static final String FILE_PATH = "src/main/java/org/ptcc/Main.java";
    private static final String TEST_PATH = "src/main/java/org/ptcc/internals/Rules/EJObjects/EJRule2.java";
    private static final String MAIN_PATH = "src/main/java/org/ptcc/internals/Collections/Violation.java";
    public static void main(String[] args) throws IOException {
        System.out.println(ImplementationScanner.getImplementations());
         EJRule68 rule = new EJRule68();
        testRuleNode(rule);

    }
    private static void testRuleNode(Rule rule) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(Paths.get(MAIN_PATH));
        List<Violation> violations = new ArrayList<>();

        /*cu.findAll(Node.class).forEach(c -> {
            rule.check(c, violations);
        });*/
        rule.check(cu, violations);

        violations.forEach(v -> System.out.println("  " + v.getMessage()));
    }
}

/*
    Parse Java File
       │
       ▼
    For Each Class:
           │
           ▼
    Classify Class (UTILITY, BUILDER, STATIC_FACTORY, etc.)
           │
           ▼
    Get Rules for That Type
           │
           ▼
    Sort Rules by Priority (CRITICAL first)
           │
           ▼
    Apply Rules in Priority Order
           │
           ▼
    Collect Violations with Severity
           │
           ▼
    Output Report
 */