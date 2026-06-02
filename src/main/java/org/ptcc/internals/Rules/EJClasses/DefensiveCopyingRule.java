package org.ptcc.internals.Rules.EJClasses;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DefensiveCopyingRule implements Rule {
    private static final List<String> MUTABLE_TYPES = Arrays.asList(
            "Date", "Calendar", "ArrayList", "HashMap", "HashSet"
    );

    @Override
    public void check(Node node, List<Violation> violations) throws IOException {
            if (!(node instanceof MethodDeclaration method)) {
                return;
            }

            String returnType = method.getType().asString();

            if (returnType.contains("[]")) {
                checkArrayReturn(method, violations);
            }

            for (String mutableType : MUTABLE_TYPES) {
                if (returnType.contains(mutableType)) {
                    checkMutableReturn(method, violations, mutableType);
                    break;
                }
            }
        }

        private void checkArrayReturn(MethodDeclaration method, List<Violation> violations) {
            String methodName = method.getNameAsString().toLowerCase();
            if (!methodName.contains("copy") && !methodName.contains("clone")) {
                violations.add(new Violation.Builder(
                        "Method '" + method.getNameAsString() +
                                "' returns an array. Consider returning a defensive copy.",
                        Severity.SUGGESTION
                ).at(method).build());
            }
        }
        private void checkMutableReturn(MethodDeclaration method, List<Violation> violations, String type) {
            String methodName = method.getNameAsString().toLowerCase();
            if (!methodName.contains("copy") && !methodName.contains("unmodifiable")) {
                violations.add(new Violation.Builder(
                        "Method '" + method.getNameAsString() +
                                "' returns " + type + ". Consider returning a copy or unmodifiable view.",
                        Severity.SUGGESTION
                ).at(method).build());
            }
        }
    }
