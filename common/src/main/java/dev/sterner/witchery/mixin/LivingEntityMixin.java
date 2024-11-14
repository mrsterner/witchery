package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.handler.PoppetHandler;
import dev.sterner.witchery.handler.VampireHandler;
import dev.sterner.witchery.mixin_logic.LivingEntityMixinLogic;
import dev.sterner.witchery.platform.ManifestationPlayerAttachment;
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float witchery$modifyHurt(float original, @Local(argsOnly = true) DamageSource damageSource) {
        var entity = LivingEntity.class.cast(this);
        return LivingEntityMixinLogic.INSTANCE.modifyHurt(entity, original, damageSource);
    }

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float witchery$modifyHurtGhost(float original, @Local(argsOnly = true) DamageSource damageSource) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        return LivingEntityMixinLogic.INSTANCE.modifyHurtGhost(livingEntity, original);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void witchery$modifyBaseTick(CallbackInfo ci) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        LivingEntityMixinLogic.INSTANCE.modifyBaseTick(livingEntity);
    }
}

