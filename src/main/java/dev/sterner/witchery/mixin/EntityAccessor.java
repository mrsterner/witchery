package dev.sterner.witchery.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("eyeHeight")
    float witchery$getEyeHeight();

    @Accessor("eyeHeight")
    void witchery$setEyeHeight(float value);
}