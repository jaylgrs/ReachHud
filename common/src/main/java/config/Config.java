package config;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Config {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("reachhud");

    private static final String HUD_ENABLED = "hud.enabled";

    private static Path configPath;
    private static boolean enabled = true;

    private Config() {
    }

    public static void initialize(Path configDirectory) {
        configPath = configDirectory.resolve("reachhud.properties");
        load();
    }

    public static void load() {
        if (configPath == null) {
            return;
        }

        if (!Files.exists(configPath)) {
            save();
            return;
        }

        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);

            String value = properties.getProperty(HUD_ENABLED, "true").trim();

            if (value.equalsIgnoreCase("true")) {
                enabled = true;
            } else if (value.equalsIgnoreCase("false")) {
                enabled = false;
            } else {
                enabled = true;
                LOGGER.warn("Invalid value for '{}'. Using default: true.", HUD_ENABLED);
            }

        } catch (IOException | IllegalArgumentException exception) {
            enabled = true;
            LOGGER.error("Failed to load ReachHUD configuration.", exception);
        }
    }

    public static void save() {
        if (configPath == null) {
            return;
        }

        Properties properties = new Properties();
        properties.setProperty(
                HUD_ENABLED,
                Boolean.toString(enabled)
        );

        try {
            Files.createDirectories(configPath.getParent());

            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "ReachHUD Configuration");
            }

        } catch (IOException exception) {
            LOGGER.error("Failed to save ReachHUD configuration.", exception);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        Config.enabled = enabled;
        save();
    }
}
