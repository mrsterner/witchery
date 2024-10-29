package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.handler.PoppetHandler;
import dev.sterner.witchery.platform.poppet.VoodooPoppetData;
import dev.sterner.witchery.platform.poppet.VoodooPoppetDataAttachment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float witchery$modifyHurt(float original, @Local(argsOnly = true) DamageSource damageSource) {
        return PoppetHandler.INSTANCE.handleVampiricPoppet((LivingEntity) (Object) this, damageSource, original);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void witchery$modifyBaseTick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        var prevData = VoodooPoppetDataAttachment.getPoppetData(livingEntity);
        if (prevData.isUnderWater())  {
            VoodooPoppetDataAttachment.setPoppetData(livingEntity, new VoodooPoppetData(false));
        }
    }
}
