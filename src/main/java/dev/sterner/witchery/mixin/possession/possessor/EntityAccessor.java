package dev.sterner.witchery.mixin.possession.possessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("collide")
    Vec3 requiem$invokeAdjustMovementForCollisions(Vec3 movement);

    @Accessor("stuckSpeedMultiplier")
    Vec3 requiem$getMovementMultiplier();

    @Invoker("getInputVector")
    static Vec3 requiem$invokeMovementInputToVelocity(Vec3 movementInput, float speed, float yaw) {
        throw new IllegalStateException(movementInput + "" + speed + "" + yaw);
    }
}
