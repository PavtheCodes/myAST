package org.ptcc.internals.Config;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

public class JavaParserConfig {
    private final String FILE_PATH = "src/main/java";


    public JavaParserConfig() {
        init();
    }
    public void init() {
        TypeSolver typeSolver = new CombinedTypeSolver();

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
    }
}
