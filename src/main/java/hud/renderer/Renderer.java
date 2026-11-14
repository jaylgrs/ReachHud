import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public final class Renderer {

    private static final double SMOOTH_SPEED = 0.25;
    private static final double FADE_SPEED = 0.75;
    private static final double MAX_DISTANCE_ADVANCE = 3.0;

    private static long lastFrameTime = System.nanoTime();

    private Renderer() {
    }

    public static void register() {
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CROSSHAIR,
                ReachHUD.id("reach_hud"),
                Renderer::render
        );
    }

    private static void render(
            GuiGraphicsExtractor graphics,
            DeltaTracker deltaTracker
    ) {
        Minecraft client = Minecraft.getInstance();

        double deltaSeconds = getDeltaSeconds();

        if (client.player == null) {
            RenderState.resetSmoothing();
            return;
        }

        renderNotification(graphics, client);

        if (!Keybind.isEnabled()) {
            updateHudAlpha(false, deltaSeconds);
            renderCachedHud(graphics, client);

            if (RenderState.hudAlpha <= 0.001) {
                RenderState.clearMeleeCache();
                RenderState.clearProjectileCache();
            }

            RenderState.resetSmoothingValuesOnly();
            return;
        }

        if (AimTracker.hasTarget()) {
            renderProjectileHud(
                    graphics,
                    client,
                    deltaSeconds
            );

            return;
        }

        RenderState.clearProjectileCache();

        renderMeleeHud(
                graphics,
                client,
                deltaSeconds
        );
    }

    private static void renderMeleeHud(
            GuiGraphicsExtractor graphics,
            Minecraft client,
            double deltaSeconds
    ) {
        Entity target = TargetTracker.getCurrentTarget();

        if (target == null) {
            updateHudAlpha(false, deltaSeconds);
            renderCachedMeleeHud(graphics, client);
            RenderState.resetMeleeSmoothingOnly();
            return;
        }

        double distance = Calculator.getDistanceTo(target);
        double reach = Calculator.getPlayerReach();

        if (distance < 0
                || reach < 0
                || distance > reach + MAX_DISTANCE_ADVANCE) {

            updateHudAlpha(false, deltaSeconds);
            renderCachedMeleeHud(graphics, client);
            RenderState.resetMeleeSmoothingOnly();
            return;
        }

        boolean withinReach = distance <= reach;

        updateMeleeSmoothing(
                target,
                distance,
                deltaSeconds
        );

        MeleeRenderer.updateCache(
                target,
                RenderState.displayedDistance,
                withinReach
        );

        RenderState.clearProjectileCache();

        updateHudAlpha(true, deltaSeconds);

        MeleeRenderer.renderCached(
                graphics,
                client,
                RenderState.hudAlpha
        );
    }

    private static void renderProjectileHud(
            GuiGraphicsExtractor graphics,
            Minecraft client,
            double deltaSeconds
    ) {
        Entity target = AimTracker.getCurrentTarget();

        if (target == null) {
            updateHudAlpha(false, deltaSeconds);
            renderCachedProjectileHud(graphics, client);
            RenderState.resetProjectileSmoothing();
            return;
        }

        double distance = AimTracker.getTargetDistance();

        if (distance < 0) {
            updateHudAlpha(false, deltaSeconds);
            renderCachedProjectileHud(graphics, client);
            RenderState.resetProjectileSmoothing();
            return;
        }

        boolean willHit = AimTracker.willHit();

        updateProjectileSmoothing(
                target,
                distance,
                deltaSeconds
        );

        ProjectileRenderer.updateCache(
                target,
                RenderState.displayedProjectileDistance,
                willHit
        );

        RenderState.clearMeleeCache();

        updateHudAlpha(true, deltaSeconds);

        ProjectileRenderer.renderCached(
                graphics,
                client,
                RenderState.hudAlpha
        );
    }

    private static void updateMeleeSmoothing(
            Entity target,
            double distance,
            double deltaSeconds
    ) {
        if (RenderState.lastTarget != target
                || RenderState.displayedDistance < 0) {

            RenderState.lastTarget = target;
            RenderState.displayedDistance = distance;
            return;
        }

        double smoothingFactor = getSmoothingFactor(
                SMOOTH_SPEED,
                deltaSeconds
        );

        RenderState.displayedDistance +=
                (distance - RenderState.displayedDistance)
                        * smoothingFactor;
    }

    private static void updateProjectileSmoothing(
            Entity target,
            double distance,
            double deltaSeconds
    ) {
        if (RenderState.lastProjectileTarget != target
                || RenderState.displayedProjectileDistance < 0) {

            RenderState.lastProjectileTarget = target;
            RenderState.displayedProjectileDistance = distance;
            return;
        }

        double smoothingFactor = getSmoothingFactor(
                SMOOTH_SPEED,
                deltaSeconds
        );

        RenderState.displayedProjectileDistance +=
                (distance
                        - RenderState.displayedProjectileDistance)
                        * smoothingFactor;
    }

    private static void updateHudAlpha(
            boolean visible,
            double deltaSeconds
    ) {
        double targetAlpha = visible ? 1.0 : 0.0;

        double smoothingFactor = getSmoothingFactor(
                FADE_SPEED,
                deltaSeconds
        );

        RenderState.hudAlpha +=
                (targetAlpha - RenderState.hudAlpha)
                        * smoothingFactor;

        if (Math.abs(
                targetAlpha - RenderState.hudAlpha
        ) < 0.001) {

            RenderState.hudAlpha = targetAlpha;
        }
    }

    private static double getSmoothingFactor(
            double speed,
            double deltaSeconds
    ) {
        return 1.0 - Math.exp(
                -speed
                        * deltaSeconds
                        * 60.0
        );
    }

    private static double getDeltaSeconds() {
        long currentTime = System.nanoTime();

        double deltaSeconds =
                (currentTime - lastFrameTime)
                        / 1_000_000_000.0;

        lastFrameTime = currentTime;

        return Math.min(
                Math.max(deltaSeconds, 0.0),
                0.1
        );
    }

    private static void renderCachedHud(
            GuiGraphicsExtractor graphics,
            Minecraft client
    ) {
        if (!RenderState.cachedMeleeTargetName.isEmpty()) {
            renderCachedMeleeHud(graphics, client);
            return;
        }

        if (!RenderState.cachedProjectileTargetName.isEmpty()) {
            renderCachedProjectileHud(graphics, client);
        }
    }

    private static void renderCachedMeleeHud(
            GuiGraphicsExtractor graphics,
            Minecraft client
    ) {
        MeleeRenderer.renderCached(
                graphics,
                client,
                RenderState.hudAlpha
        );
    }

    private static void renderCachedProjectileHud(
            GuiGraphicsExtractor graphics,
            Minecraft client
    ) {
        ProjectileRenderer.renderCached(
                graphics,
                client,
                RenderState.hudAlpha
        );
    }

    private static void renderNotification(
            GuiGraphicsExtractor graphics,
            Minecraft client
    ) {
        if (!Notification.isVisible()) {
            return;
        }

        String text = Notification.isEnabled()
                ? "ReachHUD: Active"
                : "ReachHUD: Inactive";

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int textWidth = client.font.width(text);

        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight - 58;

        int textColor = Notification.isEnabled()
                ? 0xFF55FF55
                : 0xFFFF5555;

        Component component = Component.literal(text);

        graphics.text(
                client.font,
                component,
                x + 1,
                y + 1,
                0xAA000000,
                false
        );

        graphics.text(
                client.font,
                component,
                x,
                y,
                textColor,
                false
        );
    }
}
