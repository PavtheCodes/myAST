package org.ptcc.internals.Rules;

import com.github.javaparser.ast.Node;
import org.ptcc.internals.Rule;

public interface TwoPassRule extends Rule {
    void firstPass(Node node);
    default void printReport() {}
}
