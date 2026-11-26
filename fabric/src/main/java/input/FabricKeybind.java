package input;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class FabricKeybind {

    private static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(
                    Identifier.fromNamespaceAndPath(
                            "reachhud",
                            "reachhud"
                    )
            );

    private FabricKeybind() {
    }

    public static void register() {
        KeyMapping key = new KeyMapping(
                "key.reachhud.toggle",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_RCONTROL,
                CATEGORY
        );

        Keybind.setKey(
                KeyMappingHelper.registerKeyMapping(key)
        );
    }
}
