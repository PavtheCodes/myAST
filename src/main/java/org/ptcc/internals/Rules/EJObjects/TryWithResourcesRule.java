package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.resolution.types.ResolvedType;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TryWithResourcesRule implements Rule {
    @Override
    public void check(Node node, List<Violation> violations) throws IOException {
        if (!(node instanceof CompilationUnit cu)) {
            return;
        }
        List<TryStmt> tryStatementList = cu.findAll(TryStmt.class);
        isResourceClosed(tryStatementList, violations);
    }

    public boolean isFinallyBlockPresent(TryStmt tryStatement) {
        Optional<BlockStmt> finallyBlock = tryStatement.getFinallyBlock();
        return finallyBlock.isPresent();
    }

    public void isResourceClosed(List<TryStmt> tryStatementList, List<Violation> violations) {
        for(TryStmt tryStatement : tryStatementList) {
            if(!isFinallyBlockPresent(tryStatement)) {
                continue;
            }

            BlockStmt finallyBlock = tryStatement.getFinallyBlock().get();

            boolean hasCloseCall = finallyBlock.findAll(MethodCallExpr.class).stream()
                    .anyMatch(call -> call.getNameAsString().equals("close"));

            if(hasCloseCall) {
                violations.add(new Violation.Builder(
                        "Consider try-with-resources over try-finally",
                        Severity.LOW
                ).at(tryStatement).build());
            }
        }
    }
}
