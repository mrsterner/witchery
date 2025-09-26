package dev.sterner.witchery.mixin.possession.possessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("stuckSpeedMultiplier")
    Vec3 witchery$getMovementMultiplier();
}
