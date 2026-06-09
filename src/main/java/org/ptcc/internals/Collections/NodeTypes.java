package org.ptcc.internals.Collections;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import org.ptcc.Main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum NodeTypes {

    CLASS(ClassOrInterfaceDeclaration.class),
    METHOD(MethodDeclaration.class),
    FIELD(FieldDeclaration.class),
    PARAMETER(Parameter.class),
    ENUM(EnumDeclaration.class),
    VARIABLE(VariableDeclarator.class);

    private final Class<? extends Node> nodeClass;
    private static final String PATH = "src/main/java/org/ptcc/internals/Collections/NodeTypes.java";

    NodeTypes(Class<? extends Node> nodeClass) {
        this.nodeClass = nodeClass;
    }

    public Class<? extends Node> getNodeClass() {
        return nodeClass;
    }
    public static EnumDeclaration fetchSelf() throws IOException {
        CompilationUnit node = StaticJavaParser.parse(Paths.get(PATH));
        List<EnumDeclaration> allEnums = node.findAll(EnumDeclaration.class);

        EnumDeclaration targetEnum = allEnums.stream()
                .filter(enumDecl -> enumDecl.getNameAsString().equals("NodeTypes"))
                .findFirst()
                .orElse(null);
        return targetEnum;
    }
    @Deprecated
    public static List<? extends Node> typesList() throws IOException {
        List<Node> types = new ArrayList<>();
        fetchSelf().findAll(EnumConstantDeclaration.class)
                .forEach(constant -> {
                    NodeList<Expression> args = constant.getArguments();
                    types.add(constant.getArgument(0));
                    System.out.println(types);
                    if (!args.isEmpty()) {
                        args.forEach(arg -> {
                            System.out.println(arg.toString());
                            });
                        }
                    });
            return types;
        }
    }
