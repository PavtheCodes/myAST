package org.ptcc.internals.Collections;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public final class Violation {
    private final String filePath;
    private final int lineNum;
    private final String message;
    private final Severity severity;

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public static class Builder {
        private final String message;
        private final Severity severity;

        private String filePath = "";
        private int lineNum = -1;

        public Builder(String message, Severity severity) {
            this.message = message;
            this.severity = severity;
        }

        public Builder at(Node node) {
            if (node == null) {
                return this;
            }

            lineNum = node.getBegin().map(position -> position.line).orElse(lineNum);
            filePath = node.findCompilationUnit()
                    .flatMap(CompilationUnit::getStorage)
                    .map(storage -> storage.getPath().toAbsolutePath().normalize().toString())
                    .orElseGet(() -> {
                        String currentFilePath = ViolationContext.getCurrentFilePath();
                        return currentFilePath == null ? filePath : currentFilePath;
                    });
            return this;
        }

        public Builder filePath(String val)
            { filePath = val; return this; }

        public Builder lineNum(int val)
        { lineNum = val; return this;  }
        public Violation build() {
            return new Violation(this);
        }
    }
    private Violation(Builder builder) {
        message = builder.message;
        severity = builder.severity;
        filePath = builder.filePath;
        lineNum = builder.lineNum;
    }

    @Override
    public String toString() {
        String location = filePath == null || filePath.isBlank()
                ? "unknown"
                : filePath + (lineNum > 0 ? ":" + lineNum : "");

        return "[" + severity + "] " + location + " - " + message;
    }
}
