package org.ptcc.internals.Config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public final class AnalyzerConfig {
    public enum Mode {
        AUTOMATIC,
        MANUAL
    }

    private static final String SETTINGS_RESOURCE = "settings.yaml";
    private static volatile AnalyzerConfig instance;

    private final Mode mode;
    private final int ejRule2ParameterThreshold;
    private final boolean namingConventionDetectionEnabled;

    private AnalyzerConfig(Mode mode, int ejRule2ParameterThreshold, boolean namingConventionDetectionEnabled) {
        this.mode = mode;
        this.ejRule2ParameterThreshold = ejRule2ParameterThreshold;
        this.namingConventionDetectionEnabled = namingConventionDetectionEnabled;
    }

    public static AnalyzerConfig getInstance() {
        AnalyzerConfig local = instance;
        if (local == null) {
            synchronized (AnalyzerConfig.class) {
                local = instance;
                if (local == null) {
                    instance = local = load();
                }
            }
        }
        return local;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isAutomaticMode() {
        return mode == Mode.AUTOMATIC;
    }

    public int getEjRule2ParameterThreshold() {
        return ejRule2ParameterThreshold;
    }

    public boolean isNamingConventionDetectionEnabled() {
        return namingConventionDetectionEnabled;
    }

    private static AnalyzerConfig load() {
        Yaml yaml = new Yaml();

        try (InputStream input = openSettingsStream()) {
            if (input == null) {
                throw new IllegalStateException("Could not find settings.yaml on the classpath or in src/main/resources.");
            }

            Object loaded = yaml.load(input);
            if (!(loaded instanceof Map<?, ?> configMap)) {
                return new AnalyzerConfig(Mode.AUTOMATIC, 4, true);
            }

            Mode mode = parseMode(configMap.get("mode"), configMap.get("automaticMode"));
            int threshold = parseThreshold(configMap);
            boolean namingConventionDetectionEnabled = parseNamingConventionEnabled(configMap);
            return new AnalyzerConfig(mode, threshold, namingConventionDetectionEnabled);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load analyzer configuration.", e);
        }
    }

    private static InputStream openSettingsStream() throws IOException {
        ClassLoader classLoader = AnalyzerConfig.class.getClassLoader();
        InputStream fromClasspath = classLoader.getResourceAsStream(SETTINGS_RESOURCE);
        if (fromClasspath != null) {
            return fromClasspath;
        }

        Path resourcePath = Paths.get("src", "main", "resources", SETTINGS_RESOURCE);
        if (Files.exists(resourcePath)) {
            return Files.newInputStream(resourcePath);
        }

        return null;
    }

    private static Mode parseMode(Object modeValue, Object legacyAutomaticModeValue) {
        if (modeValue instanceof String modeText) {
            return parseModeText(modeText);
        }

        if (legacyAutomaticModeValue instanceof Boolean automaticMode) {
            return automaticMode ? Mode.AUTOMATIC : Mode.MANUAL;
        }

        return Mode.AUTOMATIC;
    }

    private static Mode parseModeText(String modeText) {
        String normalized = modeText.trim().toLowerCase(Locale.ROOT);
        if ("manual".equals(normalized)) {
            return Mode.MANUAL;
        }
        return Mode.AUTOMATIC;
    }

    private static int parseThreshold(Map<?, ?> configMap) {
        Object rulesValue = configMap.get("rules");
        if (rulesValue instanceof Map<?, ?> rulesMap) {
            Object ejRule2Value = rulesMap.get("ConstructorParameterThresholdRule");
            if (ejRule2Value instanceof Map<?, ?> ejRule2Map) {
                Object thresholdValue = ejRule2Map.get("parameterThreshold");
                if (thresholdValue instanceof Number number) {
                    return number.intValue();
                }
                if (thresholdValue instanceof String text) {
                    try {
                        return Integer.parseInt(text.trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return 4;
    }

    private static boolean parseNamingConventionEnabled(Map<?, ?> configMap) {
        Object rulesValue = configMap.get("rules");
        if (rulesValue instanceof Map<?, ?> rulesMap) {
            Object namingConventionValue = rulesMap.get("namingConventionDetection");
            if (namingConventionValue instanceof Map<?, ?> namingConventionMap) {
                Object enabledValue = namingConventionMap.get("enabled");
                if (enabledValue instanceof Boolean enabled) {
                    return enabled;
                }
                if (enabledValue instanceof String text) {
                    return Boolean.parseBoolean(text.trim());
                }
            }
            Object legacyEnabledValue = rulesMap.get("namingConventionDetectionEnabled");
            if (legacyEnabledValue instanceof Boolean enabled) {
                return enabled;
            }
            if (legacyEnabledValue instanceof String text) {
                return Boolean.parseBoolean(text.trim());
            }
        }
        return true;
    }
}
