package org.ptcc.internals.Collections;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class UtilClass {
    private static UtilClass instance;
    private UtilClass() {}
    public static boolean checkNodeForClass(Node node) {
        if(!(node instanceof ClassOrInterfaceDeclaration) || ((ClassOrInterfaceDeclaration) node).isInterface()) {
            return false;
        }
        return true;
    }
    public static UtilClass getInstance() {
        return instance;
    }
}
