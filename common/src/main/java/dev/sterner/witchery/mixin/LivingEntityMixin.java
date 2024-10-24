package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.sterner.witchery.handler.PoppetHandler;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float witchery$modifyHurt(float original) {
        return PoppetHandler.INSTANCE.handleVampiricPoppet((LivingEntity) (Object) this, original);
    }
}
