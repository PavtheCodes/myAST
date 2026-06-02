package org.ptcc.internals.Rules.EJClasses;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.io.IOException;
import java.util.List;

class LocalVariableScopeRule implements Rule {
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

            Node grandParent = varDecl.getParentNode().orElse(null);
            if (grandParent instanceof BlockStmt block) {
                int index = block.getStatements().indexOf(varDecl);
                if (index < 3 && !isUsedLater(var, method)) {
                    violations.add(new Violation.Builder(
                            "Variable '" + var.getNameAsString() +
                                    "' is declared too early. Move it closer to where it's used.",
                            Severity.SUGGESTION
                    ).at(var).build());
                }
            }
        }
    }
    private boolean isUsedLater(VariableDeclarator var, MethodDeclaration method) {
        String varName = var.getNameAsString();
        return method.toString().indexOf(varName) > var.getRange().get().begin.line;
    }
}