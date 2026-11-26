package hud.renderer;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;

public final class MeleeRenderer {

    private static final String INDICATOR = "● ";
    private static final String REACH_LABEL = "Reach: ";

    private static final int TARGET_Y_OFFSET = 11;
    private static final int REACH_Y_OFFSET = 23;

    private MeleeRenderer() {
    }

    public static void updateCache(
            Entity target,
            double distance,
            boolean withinReach
    ) {
        if (target == null) {
            return;
        }

        RenderState.cachedMeleeTargetName =
                target.getName().getString();

        RenderState.cachedMeleeDistanceText =
                String.format(
                        Locale.ROOT,
                        "%.2f",
                        distance
                );

        RenderState.cachedMeleeWithinReach =
                withinReach;

        RenderState.cachedMeleeTargetColor =
                RenderUtils.getTargetNameColor(target);
    }

    public static void renderCached(
            GuiGraphicsExtractor graphics,
            Minecraft client,
            double alpha
    ) {
        if (RenderState.cachedMeleeTargetName.isEmpty()
                || RenderState.cachedMeleeDistanceText.isEmpty()) {
            return;
        }

        renderHud(
                graphics,
                client,
                RenderState.cachedMeleeTargetName,
                RenderState.cachedMeleeDistanceText,
                RenderState.cachedMeleeWithinReach,
                RenderState.cachedMeleeTargetColor,
                alpha
        );
    }

    private static void renderHud(
            GuiGraphicsExtractor graphics,
            Minecraft client,
            String targetName,
            String distanceText,
            boolean withinReach,
            int targetNameColor,
            double alpha
    ) {
        int indicatorWidth = client.font.width(INDICATOR);
        int targetNameWidth = client.font.width(targetName);
        int reachLabelWidth = client.font.width(REACH_LABEL);
        int distanceWidth = client.font.width(distanceText);

        int targetTotalWidth =
                indicatorWidth + targetNameWidth;

        int reachTotalWidth =
                reachLabelWidth + distanceWidth;

        int screenWidth =
                client.getWindow().getGuiScaledWidth();

        int screenHeight =
                client.getWindow().getGuiScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        int targetY = centerY + TARGET_Y_OFFSET;
        int reachY = centerY + REACH_Y_OFFSET;

        int targetX =
                centerX - targetTotalWidth / 2;

        int reachX =
                centerX - reachTotalWidth / 2;

        int distanceColor =
                withinReach
                        ? 0xFF55FF55
                        : 0xFFFF5555;

        int fadedIndicatorColor =
                RenderUtils.applyAlpha(
                        targetNameColor,
                        alpha
                );

        int fadedTargetColor =
                RenderUtils.applyAlpha(
                        0xFFFFFFFF,
                        alpha
                );

        int fadedLabelColor =
                RenderUtils.applyAlpha(
                        0xFFCCCCCC,
                        alpha
                );

        int fadedDistanceColor =
                RenderUtils.applyAlpha(
                        distanceColor,
                        alpha
                );

        RenderUtils.drawText(
                graphics,
                client,
                INDICATOR,
                targetX,
                targetY,
                fadedIndicatorColor,
                alpha
        );

        RenderUtils.drawText(
                graphics,
                client,
                targetName,
                targetX + indicatorWidth,
                targetY,
                fadedTargetColor,
                alpha
        );

        RenderUtils.drawText(
                graphics,
                client,
                REACH_LABEL,
                reachX,
                reachY,
                fadedLabelColor,
                alpha
        );

        RenderUtils.drawText(
                graphics,
                client,
                distanceText,
                reachX + reachLabelWidth,
                reachY,
                fadedDistanceColor,
                alpha
        );
    }
}