package dev.sterner.witchery.mixin;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface WalkAnimationStateAccessor {

    @Accessor("position")
    float getWalkPosition();

    @Accessor("position")
    void setWalkPosition(float position);

    @Accessor("speed")
    float getWalkSpeed();

    @Accessor("speed")
    void setWalkSpeed(float speed);

    @Accessor("speedOld")
    float getWalkSpeedOld();

    @Accessor("speedOld")
    void setWalkSpeedOld(float speedOld);
}
