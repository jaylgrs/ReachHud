package input;
import config.Config;
import hud.Notification;
import net.minecraft.client.KeyMapping;

public final class Keybind {

    private static KeyMapping toggleKey;

    private Keybind() {
    }

    public static void setKey(KeyMapping key) {
        toggleKey = key;
    }

    public static void update() {
        if (toggleKey == null) {
            return;
        }

        while (toggleKey.consumeClick()) {
            boolean newState = !Config.isEnabled();

            Config.setEnabled(newState);
            Notification.show(newState);
        }
    }

    public static boolean isEnabled() {
        return Config.isEnabled();
    }
}
