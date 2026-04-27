package org.ptcc.internals.Collections;

import lombok.Getter;

import java.lang.annotation.*;

public final class Violation {
    private final String filePath;
    private final int lineNum;
    private final String message;
    private final Severity severity;
    @Deprecated
    public String test, test2;
    @Deprecated
    public String test3, test4;
    @Deprecated
    public String test5, test6;
    @Getter
    public String getterField;

    public String getFilePath() {
        return filePath;
    }

    public int getLineNum() {
        return lineNum;
    }

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
        return "Violation{" +
                "filePath='" + filePath + '\'' +
                ", lineNum=" + lineNum +
                ", message='" + message + '\'' +
                ", severity=" + severity +
                '}';
    }
}
