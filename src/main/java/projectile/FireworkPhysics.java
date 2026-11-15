
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class FireworkPhysics {

    private static final double MAX_DISTANCE = 128.0;
    private static final double HORIZONTAL_ACCELERATION = 1.15;
    private static final double VERTICAL_ACCELERATION = 0.04;
    private static final double PROJECTILE_HALF_SIZE = 0.125;

    private FireworkPhysics() {
    }

    public static TrajectoryResult simulate(
            Vec3 startPosition,
            Vec3 initialVelocity,
            AABB targetBox
    ) {
        if (startPosition == null
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

            var hit = expandedTarget.clip(
                    position,
                    nextPosition
            );

            if (hit.isPresent()) {
                Vec3 hitPosition = hit.get();

                double hitDistance =
                        travelledDistance
                                + position.distanceTo(hitPosition);

                return TrajectoryResult.hit(
                        hitPosition,
                        hitDistance
                );
            }

            travelledDistance +=
                    position.distanceTo(nextPosition);

            if (reachesMaxDistance) {
                break;
            }

            position = nextPosition;

            velocity = new Vec3(
                    velocity.x * HORIZONTAL_ACCELERATION,
                    velocity.y + VERTICAL_ACCELERATION,
                    velocity.z * HORIZONTAL_ACCELERATION
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