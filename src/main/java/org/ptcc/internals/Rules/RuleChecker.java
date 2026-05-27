package org.ptcc.internals.Rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Collections.ViolationContext;
import org.ptcc.internals.Rule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuleChecker {

    private final List<Rule> rules;

    public RuleChecker(List<Rule> rules) {
        this.rules = rules;
    }

    public List<Violation> runCheck(Path projectRootPath) throws IOException {
        List<Violation> violations = new ArrayList<>();

        System.out.println("Scanning directory: " + projectRootPath.toAbsolutePath());

        Files.walk(projectRootPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(file -> {
                    ViolationContext.setCurrentFilePath(file.toAbsolutePath().normalize().toString());
                    try {
                        System.out.println("\n=== Processing file: " + file + " ===");
                        CompilationUnit cu = StaticJavaParser.parse(file);

                        for (Rule rule : rules) {
                            rule.check(cu, violations);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        System.err.println("Failed to parse file: " + file);
                        e.printStackTrace();
                    } finally {
                        ViolationContext.clear();
                    }
                });

        System.out.println("\n=== Total violations found: " + violations.size() + " ===");
        return violations;
    }
}
