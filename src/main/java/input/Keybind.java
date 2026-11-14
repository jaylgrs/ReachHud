import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class Keybind {

    private static final KeyMapping.Category REACHHUD_CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            ReachHUD.MOD_ID,
                            "reachhud"
                    )
            );

    private static KeyMapping toggleKey;

    private Keybind() {
    }

    public static void register() {
        if (toggleKey != null) {
            return;
        }

        toggleKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.reachhud.toggle",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_RCONTROL,
                        REACHHUD_CATEGORY
                )
        );
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
