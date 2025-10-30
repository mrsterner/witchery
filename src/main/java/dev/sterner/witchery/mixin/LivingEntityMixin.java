package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import dev.sterner.witchery.core.api.interfaces.EntityChainInterface;
import dev.sterner.witchery.core.api.interfaces.OnRemovedEffect;
import dev.sterner.witchery.content.entity.ChainEntity;
import dev.sterner.witchery.core.registry.WitcheryMobEffects;
import dev.sterner.witchery.features.death.DeathPlayerAttachment;
import dev.sterner.witchery.features.death.DeathTransformationHelper;
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment;
import dev.sterner.witchery.core.registry.WitcheryTags;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.blood.BloodPoolHandler;
import dev.sterner.witchery.features.necromancy.NecroHandler;
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler;
import dev.sterner.witchery.features.affliction.AfflictionTypes;
import dev.sterner.witchery.features.affliction.event.TransformationHandler;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EntityChainInterface {

    @Unique
    private final List<Pair<ChainEntity, Boolean>> witchery$restrainingChains = new ArrayList<>();
    @Unique
    private boolean witchery$restrained = false;

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void witchery$preventMovement(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(WitcheryMobEffects.INSTANCE.getBEAR_TRAP_INCAPACITATED())) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            ci.cancel();
        }
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"))
    private void witchery$preventKnockback(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(WitcheryMobEffects.INSTANCE.getBEAR_TRAP_INCAPACITATED())) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.0, 1.0, 0.0));
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void witchery$deathBootsLavaImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            if (DeathTransformationHelper.INSTANCE.hasDeathBoots(player) && DeathPlayerAttachment.getData(player).getHasDeathFluidWalking()) {
                if (source.is(DamageTypes.LAVA)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void witchery$deathBootsUnderwaterBreathing(int air, CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            if (DeathTransformationHelper.INSTANCE.isDeath(player)) {
                cir.setReturnValue(air);
            }
        }
    }

    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void witchery$deathBootsMaxAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            if (DeathTransformationHelper.INSTANCE.isDeath(player)) {
                cir.setReturnValue(entity.getMaxAirSupply());
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void witchery$deathRobeFireProtection(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            if (DeathTransformationHelper.INSTANCE.hasDeathRobe(player)) {
                if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.LAVA) ||
                        source.is(DamageTypes.HOT_FLOOR) || source.is(DamageTypes.IN_FIRE) ||
                        source.is(DamageTypes.ON_FIRE)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void witchery$preventSwingForAbility(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Player player && AfflictionAbilityHandler.INSTANCE.getSelectedAbility(player) != null) {
            ci.cancel();
        }
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void witchery$modifyBaseTick(CallbackInfo ci) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        BloodPoolHandler.INSTANCE.tickBloodRegen(livingEntity);
        NecroHandler.INSTANCE.tickLiving(livingEntity);
    }

    @Inject(method = "onEffectRemoved", at = @At("HEAD"))
    private void witchery$onEffectRemoved(CallbackInfo ci, @Local(argsOnly = true) MobEffectInstance instance) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        if (instance.getEffect().value() instanceof OnRemovedEffect removedEffect) {
            removedEffect.onRemovedEffect(livingEntity);
        }
    }

    @Override
    public void witchery$restrainMovement(@NotNull ChainEntity chainEntity, boolean totalRestrict) {
        witchery$restrainingChains.removeIf(pair -> pair.getFirst().equals(chainEntity));
        witchery$restrainingChains.add(Pair.of(chainEntity, totalRestrict));
        witchery$restrained = true;
    }

    @Override
    public boolean witchery$isRestrained() {
        witchery$restrainingChains.removeIf(pair -> pair.getFirst().isRemoved());

        witchery$restrained = !witchery$restrainingChains.isEmpty();
        return witchery$restrained;
    }

    @Override
    public @NotNull List<ChainEntity> witchery$getRestrainingChains() {
        witchery$restrainingChains.removeIf(pair -> pair.getFirst().isRemoved());

        return witchery$restrainingChains.stream()
                .map(Pair::getFirst)
                .toList();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void witchery$onTick(CallbackInfo ci) {
        if (witchery$isRestrained()) {
            LivingEntity self = (LivingEntity) (Object) this;
            if (self instanceof Mob mob) {
                mob.getNavigation().stop();
            }
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void witchery$onTravel(Vec3 travelVector, CallbackInfo ci) {
        if (witchery$isRestrained()) {
            LivingEntity self = (LivingEntity) (Object) this;
            self.walkAnimation.setSpeed(0);
            self.walkAnimation.update(0, 0.0F);

            if (self instanceof Mob mob) {
                mob.getNavigation().stop();
            }
            if (witchery$restrainingChains.stream().anyMatch(Pair::getSecond)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void witchery$restrainMotion(CallbackInfo ci) {
        if (witchery$isRestrained()) {
            LivingEntity self = (LivingEntity) (Object) this;
            self.zza = 0.0F;
            self.xxa = 0.0F;
            self.yya = 0.0F;
            self.setJumping(false);
        }
    }

    @ModifyReturnValue(method = "shouldDropLoot", at = @At("RETURN"))
    private boolean witchery$shouldDropLoot(boolean original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getType().is(WitcheryTags.INSTANCE.getNECROMANCER_SUMMONABLE())) {
            return original && EtherealEntityAttachment.getData(self).getCanDropLoot();
        }
        return original;
    }

    @ModifyVariable(
            method = "getDamageAfterArmorAbsorb",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float witchery$bypassArmor(float damage, DamageSource source) {
        if (source.getEntity() instanceof Player player) {
            var data = AfflictionPlayerAttachment.getData(player);

            if (data.getWerewolfLevel() >= 9) {
                if (TransformationHandler.isWolf(player) ||
                        TransformationHandler.isWerewolf(player)) {
                    return damage;
                }
            }
        }
        return damage;
    }
}
