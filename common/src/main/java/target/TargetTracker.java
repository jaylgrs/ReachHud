package target;
import reach.Calculator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class TargetTracker {

    private static final double DISPLAY_ADVANCE = 3.0;

    private static Entity currentTarget;

    private TargetTracker() {
    }

    public static void update(Minecraft client) {
        if (client.player == null) {
            currentTarget = null;
            return;
        }

        Entity crosshairTarget = client.crosshairPickEntity;

        if (crosshairTarget != null) {
            currentTarget = crosshairTarget;
            return;
        }

        Entity extendedTarget = findExtendedTarget(client);

        if (extendedTarget != null) {
            currentTarget = extendedTarget;
            return;
        }

        currentTarget = null;
    }

    private static Entity findExtendedTarget(Minecraft client) {
        if (client.player == null || client.level == null) {
            return null;
        }

        double reach = Calculator.getPlayerReach();

        if (reach < 0) {
            return null;
        }

        double maxDistance = reach + DISPLAY_ADVANCE;

        Vec3 eyePosition = client.player.getEyePosition();
        Vec3 viewVector = client.player.getViewVector(1.0F);

        Vec3 endPosition = eyePosition.add(
                viewVector.scale(maxDistance)
        );

        BlockHitResult blockHit = client.level.clip(
                new ClipContext(
                        eyePosition,
                        endPosition,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        client.player
                )
        );

        double blockDistance = blockHit.getType() == HitResult.Type.MISS
                ? maxDistance
                : eyePosition.distanceTo(blockHit.getLocation());

        double closestDistance = blockDistance * blockDistance;

        AABB searchBox = client.player
                .getBoundingBox()
                .expandTowards(
                        viewVector.scale(maxDistance)
                )
                .inflate(1.0);

        Entity closestEntity = null;

        for (Entity entity : client.player.level().getEntities(
                client.player,
                searchBox,
                entity -> entity.isPickable()
                        && entity.isAlive()
                        && entity != client.player
        )) {
            AABB entityBox = entity.getBoundingBox()
                    .inflate(entity.getPickRadius());

            Optional<Vec3> hit = entityBox.clip(
                    eyePosition,
                    endPosition
            );

            if (hit.isEmpty()) {
                continue;
            }

            double distance = eyePosition.distanceToSqr(hit.get());

            if (distance < closestDistance) {
                closestDistance = distance;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }

    public static Entity getCurrentTarget() {
        return currentTarget;
    }

    public static void clearTarget() {
        currentTarget = null;
    }
}
