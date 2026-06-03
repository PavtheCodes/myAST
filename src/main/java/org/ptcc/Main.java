package org.ptcc;

import org.ptcc.internals.Collections.Violation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java -jar linter.jar <path>");
            System.exit(1);
        }

        Path path = Paths.get(args[0]);
        List<Violation> violations = Linter.analyze(path);

        System.out.println("Violations found: " + violations.size());
        violations.forEach(System.out::println);
    }
}