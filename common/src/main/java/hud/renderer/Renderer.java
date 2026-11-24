package hud.renderer;
import hud.Notification;
import input.Keybind;
import reach.Calculator;
import target.TargetTracker;
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

    public static void render(
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
            renderCachedMeleeHud(graphics, client);

            if (RenderState.hudAlpha <= 0.001) {
                RenderState.clearMeleeCache();
            }

            RenderState.resetMeleeSmoothingOnly();
            return;
        }

        renderMeleeHud(graphics, client, deltaSeconds);
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

        if (distance < 0 || reach < 0
                || distance > reach + MAX_DISTANCE_ADVANCE) {
            updateHudAlpha(false, deltaSeconds);
            renderCachedMeleeHud(graphics, client);
            RenderState.resetMeleeSmoothingOnly();
            return;
        }

        boolean withinReach = distance <= reach;

        updateMeleeSmoothing(target, distance, deltaSeconds);

        MeleeRenderer.updateCache(
                target,
                RenderState.displayedDistance,
                withinReach
        );

        updateHudAlpha(true, deltaSeconds);

        MeleeRenderer.renderCached(
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

        if (Math.abs(targetAlpha - RenderState.hudAlpha) < 0.001) {
            RenderState.hudAlpha = targetAlpha;
        }
    }

    private static double getSmoothingFactor(
            double speed,
            double deltaSeconds
    ) {
        return 1.0 - Math.exp(
                -speed * deltaSeconds * 60.0
        );
    }

    private static double getDeltaSeconds() {
        long currentTime = System.nanoTime();

        double deltaSeconds =
                (currentTime - lastFrameTime) / 1_000_000_000.0;

        lastFrameTime = currentTime;

        return Math.min(Math.max(deltaSeconds, 0.0), 0.1);
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

        graphics.text(client.font, component, x + 1, y + 1, 0xAA000000, false);
        graphics.text(client.font, component, x, y, textColor, false);
    }
}
