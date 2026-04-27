package org.ptcc.internals;

import com.github.javaparser.ast.Node;
import org.ptcc.internals.Collections.Violation;

import java.io.IOException;
import java.util.List;

public interface Rule {
    void check(Node node, List<Violation> violations) throws IOException;
}
