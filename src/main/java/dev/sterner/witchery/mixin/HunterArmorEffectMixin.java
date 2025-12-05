package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.features.hunter.HunterArmorDefenseHandler;
import dev.sterner.witchery.features.hunter.HunterArmorParticleEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class HunterArmorEffectMixin {

    @Unique
    private boolean witchery$effectModifiedByHunterArmor = false;

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MobEffectInstance witchery$modifyHarmfulEffectDuration(MobEffectInstance effectInstance) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Player player) {
            Integer reducedDuration = HunterArmorDefenseHandler.INSTANCE.getReducedEffectDuration(player, effectInstance);

            if (reducedDuration != null) {
                witchery$effectModifiedByHunterArmor = true;

                return new MobEffectInstance(
                        effectInstance.getEffect(),
                        reducedDuration,
                        effectInstance.getAmplifier(),
                        effectInstance.isAmbient(),
                        effectInstance.isVisible(),
                        effectInstance.showIcon()
                );
            }
        }

        return effectInstance;
    }

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectAdded(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V"),
            ordinal = 0,
            argsOnly = true)
    private MobEffectInstance witchery$spawnProtectionParticles(MobEffectInstance effectInstance, @Local(argsOnly = true) MobEffectInstance originalInstance) {
        if (witchery$effectModifiedByHunterArmor) {
            LivingEntity self = (LivingEntity) (Object) this;

            if (self instanceof Player player) {
                HunterArmorParticleEffects.INSTANCE.spawnProtectionParticles(
                        player,
                        HunterArmorParticleEffects.ProtectionType.POTION_REDUCTION
                );
            }

            witchery$effectModifiedByHunterArmor = false;
        }

        return effectInstance;
    }
}