import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ReachHUDClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Config.load();
        Keybind.register();
        Renderer.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Keybind.update();
            TargetTracker.update(client);
            AimTracker.update(client);
        });
    }
}