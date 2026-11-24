package neoforge;
import config.Config;
import input.Keybind;
import hud.renderer.Renderer;
import target.TargetTracker;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = "reachhud", dist = net.neoforged.api.distmarker.Dist.CLIENT)
public final class NeoForgeClient {

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            "reachhud",
                            "reachhud"
                    )
            );

    public NeoForgeClient(IEventBus modEventBus) {
        Config.initialize(FMLPaths.CONFIGDIR.get());

        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::registerGuiLayers);

        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);

        KeyMapping key = new KeyMapping(
                "key.reachhud.toggle",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_RCONTROL,
                CATEGORY
        );

        event.register(key);
        Keybind.setKey(key);
    }

    private void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.CROSSHAIR,
                Identifier.fromNamespaceAndPath(
                        "reachhud",
                        "reach_hud"
                ),
                Renderer::render
        );
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();

        Keybind.update();
        TargetTracker.update(client);
    }
}