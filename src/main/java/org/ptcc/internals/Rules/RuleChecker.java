package org.ptcc.internals.Rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Collections.ViolationContext;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class RuleChecker {

    private final List<Rule> rules;

    public RuleChecker(List<Rule> rules) {
        this.rules = rules;
    }

    public List<Violation> runCheck(Path projectRootPath) throws IOException {
        List<Violation> violations = new ArrayList<>();

        System.out.println("Scanning directory: " + projectRootPath.toAbsolutePath());

        List<TwoPassRule> twoPassRules = rules.stream()
                .filter(r -> r instanceof TwoPassRule)
                .map(r -> (TwoPassRule) r)
                .toList();
        List<Path> files = Files.walk(projectRootPath)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .toList();
        Map<Path, CompilationUnit> parsed = new LinkedHashMap<>();
        for (Path file : files) {
            try {
                parsed.put(file, StaticJavaParser.parse(file));
            } catch (Exception e) {
                System.err.println("Failed to parse file: " + file);
                e.printStackTrace();
            }
        }

        if (!twoPassRules.isEmpty()) {
            for (Map.Entry<Path, CompilationUnit> entry : parsed.entrySet()) {
                for (TwoPassRule rule : twoPassRules) {
                    rule.firstPass(entry.getValue());
                }
            }
            twoPassRules.forEach(TwoPassRule::printReport);
        }

        for (Map.Entry<Path, CompilationUnit> entry : parsed.entrySet()) {
            Path file = entry.getKey();
            ViolationContext.setCurrentFilePath(file.toAbsolutePath().normalize().toString());
            try {
                System.out.println("\n=== Processing file: " + file + " ===");
                for (Rule rule : rules) {
                    rule.check(entry.getValue(), violations);
                }
            } catch (Exception e) {
                System.err.println("Failed to check file: " + file);
                e.printStackTrace();
            } finally {
                ViolationContext.clear();
            }
        }

        System.out.println("\n=== Total violations found: " + violations.size() + " ===");
        return violations;
    }
}