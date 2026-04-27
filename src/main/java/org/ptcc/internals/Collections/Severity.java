package org.ptcc.internals.Collections;

public enum Severity {
    HIGH("High"),
    LOW("Low"),
    WARNING("Warning"),
    SUGGESTION("Suggestion"),
    INFO("Info");

    private final String displayName;
    Severity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
