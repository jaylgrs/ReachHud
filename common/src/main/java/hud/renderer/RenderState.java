package hud.renderer;
import net.minecraft.world.entity.Entity;

public final class RenderState {

    public static Entity lastTarget;
    public static double displayedDistance = -1.0;

    public static String cachedMeleeTargetName = "";
    public static String cachedMeleeDistanceText = "";
    public static boolean cachedMeleeWithinReach;
    public static int cachedMeleeTargetColor = 0xFFFFFFFF;

    public static double hudAlpha = 0.0;

    private RenderState() {
    }

    public static void resetSmoothing() {
        resetMeleeSmoothingOnly();
        clearMeleeCache();
        hudAlpha = 0.0;
    }

    public static void resetMeleeSmoothingOnly() {
        lastTarget = null;
        displayedDistance = -1.0;
    }

    public static void clearMeleeCache() {
        cachedMeleeTargetName = "";
        cachedMeleeDistanceText = "";
        cachedMeleeWithinReach = false;
        cachedMeleeTargetColor = 0xFFFFFFFF;
    }
}