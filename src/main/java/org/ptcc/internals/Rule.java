package org.ptcc.internals;

import com.github.javaparser.ast.Node;

import java.util.List;

public interface Violation {
    void check(Node node, List<org.ptcc.internals.Collections.Violation>)
}
