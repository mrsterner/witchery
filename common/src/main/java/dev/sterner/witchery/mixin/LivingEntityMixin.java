package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.api.interfaces.OnRemovedEffect;
import dev.sterner.witchery.handler.transformation.TransformationHandler;
import dev.sterner.witchery.mixin_logic.LivingEntityMixinLogic;
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private boolean witchery$shouldUpdateDim = true;

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
        BloodPoolLivingEntityAttachment.INSTANCE.tickBloodRegen(livingEntity);
    }

    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions witchery$modifyDimensions(EntityDimensions original) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        if (livingEntity instanceof Player player && TransformationHandler.isBat(player)) {
            return EntityDimensions.scalable(0.5f, 0.85f);
        }
        if (livingEntity instanceof Player player && TransformationHandler.isWolf(player)) {
            return EntityDimensions.scalable(0.6F, 0.85F);
        }
        return original;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getScale()F"))
    private void witchery$modifyScale(CallbackInfo ci) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        if (livingEntity instanceof Player player && (
                TransformationHandler.isBat(player) ||
                TransformationHandler.isWolf(player) ||
                TransformationHandler.isWerewolf(player)
        )) {
            if (witchery$shouldUpdateDim) {
                livingEntity.refreshDimensions();
                witchery$shouldUpdateDim = false;
            }
        } else {
            witchery$shouldUpdateDim = true;
        }
    }
    @Inject(method = "onEffectRemoved", at = @At("HEAD"))
    private void witchery$onEffectRemoved(CallbackInfo ci, @Local(argsOnly = true) MobEffectInstance instance) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        if (instance.getEffect().value() instanceof OnRemovedEffect removedEffect) {
            removedEffect.onRemovedEffect(livingEntity);
        }
    }
}

