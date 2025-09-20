package dev.sterner.witchery.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor("swimAmount")
    float getSwimAmount();

    @Accessor("swimAmount")
    void setSwimAmount(float value);

    @Accessor("swimAmountO")
    float getSwimAmountO();

    @Accessor("swimAmountO")
    void setSwimAmountO(float value);
}
