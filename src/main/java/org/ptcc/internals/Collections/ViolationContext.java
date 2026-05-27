package org.ptcc.internals.Collections;

public final class ViolationContext {
    private static final ThreadLocal<String> CURRENT_FILE_PATH = new ThreadLocal<>();

    public static void setCurrentFilePath(String filePath) {
        CURRENT_FILE_PATH.set(filePath);
    }

    public static String getCurrentFilePath() {
        return CURRENT_FILE_PATH.get();
    }

    public static void clear() {
        CURRENT_FILE_PATH.remove();
    }

    private ViolationContext() {
    }
}
