package org.ptcc;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rules.ImplementationScanner;
import org.ptcc.internals.Rules.RuleChecker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Main entry point for using the static code analyzer.
 *
 * <p>This class provides a simple API to analyze Java code based on rules
 * from Effective Java. All rules are loaded automatically and run on the
 * specified Java files.</p>
 *
 * <p>Use example:</p>
 * <pre>{@code
 * // Analyze an entire directory
 * Path path = Paths.get("src/main/java");
 * List<Violation> violations = Linter.analyze(path);
 *
 * // Analyze a single file
 * Path file = Paths.get("src/MyClass.java");
 * List<Violation> violations = Linter.analyze(file);
 *
 * // Print results
 * violations.forEach(System.out::println);
 * }</pre>
 *
 * <p>The following rules are implemented:</p>
 * <ul>
 *     <li><b>Item 1:</b> Static factory methods</li>
 *     <li><b>Item 2:</b> Builder pattern for many constructor parameters</li>
 *     <li><b>Item 4:</b> Private constructor for utility classes</li>
 *     <li><b>Item 6:</b> Avoid unnecessary objects</li>
 *     <li><b>Item 12:</b> Override toString() for debugging</li>
 *     <li><b>Item 16:</b> Avoid public fields, use accessors</li>
 *     <li><b>Item 24:</b> Favor static member classes</li>
 *     <li><b>Item 50:</b> Make defensive copies when returning arrays or mutable objects</li>
 *     <li><b>Item 57:</b> Minimize scope of local variables</li>
 *     <li><b>Item 68:</b> Follow naming conventions (two-pass analysis)</li>
 * </ul>
 *
 * @author Pavlos Theodoropoulos
 * @version 1.0
 */

public final class Linter {
    private Linter() {
        throw new UnsupportedOperationException("Linter class cannot be instantiated");
    }
    public static List<Violation> analyze(Path path) throws IOException {
        return analyze(path, true);
    }
    public static List<Violation> analyze(Path path, boolean enableTwoPass) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);

        RuleChecker checker = new RuleChecker(ImplementationScanner.getRuleInstances());
        return checker.runCheck(path);
    }
    public static List<Violation> analyze(String pathStr) throws IOException {
        return analyze(Path.of(pathStr));
    }

}