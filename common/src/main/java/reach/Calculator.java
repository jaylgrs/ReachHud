package reach;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Calculator {

    private Calculator() {
    }

    public static double getDistanceTo(Entity entity) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null || entity == null) {
            return -1.0;
        }

        Vec3 eyePosition = client.player.getEyePosition();

        AABB targetBox = entity.getBoundingBox();

        return Math.sqrt(targetBox.distanceToSqr(eyePosition));
    }

    public static double getPlayerReach() {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) {
            return -1.0;
        }

        return client.player.getAttributeValue(
                Attributes.ENTITY_INTERACTION_RANGE
        );
    }

    public static boolean isWithinReach(Entity entity) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null || entity == null) {
            return false;
        }

        double reach = getPlayerReach();

        if (reach < 0) {
            return false;
        }

        return entity.getBoundingBox().distanceToSqr(
                client.player.getEyePosition()
        ) <= reach * reach;
    }
}