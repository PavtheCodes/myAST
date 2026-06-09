package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

import org.ptcc.internals.Collections.Severity;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import java.util.List;


class UnnecessaryObjectRule implements Rule {
    @Override
    public void check(Node node, List<Violation> violations) {
        if (!(node instanceof CompilationUnit)) {
            return;
        }
        enableCombinedSolver();
        List<FieldDeclaration> fields = node.findAll(FieldDeclaration.class);
        for(FieldDeclaration fieldDeclaration : fields) {
            String typeName = fieldDeclaration.getVariables().get(0).getType().asString();
            if (typeName.equals("String") || typeName.equals("java.lang.String")) {
                continue;
            }
            if(fieldDeclaration.getVariables().get(0).getType().isPrimitiveType()) {
                continue;
            }
            if (fieldDeclaration.getVariables().get(0).getType().isReferenceType()) {
                violations.add(new Violation.Builder("Unnecessary object creation", Severity.LOW).at(fieldDeclaration).build());
            }

        }
        disableCombinedSolver();
    }

    public void enableCombinedSolver() {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        JavaSymbolSolver javaSymbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser
                .getParserConfiguration()
                .setSymbolResolver(javaSymbolSolver);
    }

    public void disableCombinedSolver() {
        StaticJavaParser.setConfiguration(new ParserConfiguration());
    }
}
