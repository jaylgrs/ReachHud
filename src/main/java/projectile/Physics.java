
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class Physics {

    private static final double MAX_DISTANCE = 64.0;
    private static final double ARROW_GRAVITY = 0.05;
    private static final double AIR_INERTIA = 0.99;
    private static final double PROJECTILE_HALF_SIZE = 0.125;

    private Physics() {
    }

    public static TrajectoryResult simulate(
            Level level,
            Entity source,
            Vec3 startPosition,
            Vec3 initialVelocity,
            AABB targetBox
    ) {
        if (level == null
                || source == null
                || startPosition == null
                || initialVelocity == null
                || targetBox == null) {
            return TrajectoryResult.miss();
        }

        Vec3 position = startPosition;
        Vec3 velocity = initialVelocity;

        double travelledDistance = 0.0;

        AABB expandedTarget =
                targetBox.inflate(PROJECTILE_HALF_SIZE);

        for (int tick = 0; tick < 200; tick++) {
            double segmentLength = velocity.length();

            if (segmentLength <= 0.0) {
                break;
            }

            double remainingDistance =
                    MAX_DISTANCE - travelledDistance;

            if (remainingDistance <= 0.0) {
                break;
            }

            boolean reachesMaxDistance =
                    segmentLength >= remainingDistance;

            Vec3 nextPosition = reachesMaxDistance
                    ? position.add(
                            velocity.scale(
                                    remainingDistance / segmentLength
                            )
                    )
                    : position.add(velocity);

            var targetHit = expandedTarget.clip(
                    position,
                    nextPosition
            );

            BlockHitResult blockHit =
                    level.clip(
                            new ClipContext(
                                    position,
                                    nextPosition,
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    source
                            )
                    );

            if (targetHit.isPresent()) {
                Vec3 hitPosition = targetHit.get();

                double hitDistance =
                        position.distanceTo(hitPosition);

                if (blockHit.getType() != HitResult.Type.MISS) {
                    double blockDistance =
                            position.distanceTo(
                                    blockHit.getLocation()
                            );

                    if (blockDistance <= hitDistance) {
                        return TrajectoryResult.miss();
                    }
                }

                return TrajectoryResult.hit(
                        hitPosition,
                        travelledDistance + hitDistance
                );
            }

            if (blockHit.getType() != HitResult.Type.MISS) {
                break;
            }

            travelledDistance +=
                    position.distanceTo(nextPosition);

            if (reachesMaxDistance) {
                break;
            }

            position = nextPosition;

            velocity = new Vec3(
                    velocity.x * AIR_INERTIA,
                    velocity.y * AIR_INERTIA
                            - ARROW_GRAVITY,
                    velocity.z * AIR_INERTIA
            );
        }

        return TrajectoryResult.miss();
    }

    public record TrajectoryResult(
            boolean hit,
            Vec3 hitPosition,
            double distance
    ) {

        public static TrajectoryResult hit(
                Vec3 hitPosition,
                double distance
        ) {
            return new TrajectoryResult(
                    true,
                    hitPosition,
                    distance
            );
        }

        public static TrajectoryResult miss() {
            return new TrajectoryResult(
                    false,
                    Vec3.ZERO,
                    -1.0
            );
        }
    }
}