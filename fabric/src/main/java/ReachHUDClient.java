import config.Config;
import input.Keybind;
import input.FabricKeybind;
import renderer.FabricRenderer;
import target.TargetTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

public class ReachHUDClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Config.initialize(FabricLoader.getInstance().getConfigDir());

        FabricKeybind.register();
        FabricRenderer.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Keybind.update();
            TargetTracker.update(client);
        });
    }
}