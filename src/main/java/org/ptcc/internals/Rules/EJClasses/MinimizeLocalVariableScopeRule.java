package org.ptcc.internals.Rules.EJClasses;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.List;

class MinimizeLocalVariableScopeRule implements Rule {
    @Override
    public void check(Node node, List<Violation> violations) throws IOException {
        if (!(node instanceof MethodDeclaration method)) {
            return;
        }

        List<VariableDeclarator> variables = method.findAll(VariableDeclarator.class);

        for (VariableDeclarator var : variables) {
            Node parent = var.getParentNode().orElse(null);
            if (!(parent instanceof VariableDeclarationExpr varDecl)) {
                continue;
            }

            varDecl.findAncestor(BlockStmt.class).ifPresent(block -> {
                int index = varDecl.findAncestor(Statement.class)
                        .map(block.getStatements()::indexOf)
                        .orElse(-1);
                if (index < 3 && isUsedLater(var, method)) {
                    violations.add(new Violation.Builder(
                            "Variable '" + var.getNameAsString() +
                                    "' is declared too early. Move it closer to where it's used.",
                            Severity.SUGGESTION
                    ).at(var).build());
                }
            });
        }
    }
    private boolean isUsedLater(VariableDeclarator var, MethodDeclaration method) {
        String varName = var.getNameAsString();
        int declarationLine = var.getRange()
                .map(range -> range.begin.line)
                .orElse(Integer.MAX_VALUE);

        return method.findAll(NameExpr.class).stream()
                .filter(name -> name.getNameAsString().equals(varName))
                .map(name -> name.getRange().map(range -> range.begin.line).orElse(Integer.MAX_VALUE))
                .anyMatch(line -> line > declarationLine);
    }
}
