package org.ptcc;


import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;
import org.ptcc.internals.Rules.ImplementationScanner;
import org.ptcc.internals.Rules.RuleChecker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



public class Main {
    private static final String MAIN_PATH = "src/main/java/org/ptcc/internals/Collections/Violation.java";
    public static void main(String[] args) throws IOException {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);
        RuleChecker newCheck = new RuleChecker(ImplementationScanner.getRuleInstances());
        List<Violation> violations = newCheck.runCheck(Path.of("src/main/java/org/ptcc/internals"));
        violations.forEach(System.out::println);

        


    }
    private static void testRuleNode(Rule rule) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(Paths.get(MAIN_PATH));
        List<Violation> violations = new ArrayList<>();
        rule.check(cu, violations);

        violations.forEach(v -> System.out.println("  " + v.getMessage() + "\n"));
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
