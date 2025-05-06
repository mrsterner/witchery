package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import dev.sterner.witchery.api.EntityChainInterface;
import dev.sterner.witchery.api.interfaces.OnRemovedEffect;
import dev.sterner.witchery.entity.BansheeEntity;
import dev.sterner.witchery.entity.ChainEntity;
import dev.sterner.witchery.handler.transformation.TransformationHandler;
import dev.sterner.witchery.mixin_logic.LivingEntityMixinLogic;
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityChainInterface {

    @Unique
    private boolean witchery$shouldUpdateDim = true;
    @Unique
    private final List<Pair<ChainEntity, Boolean>> witchery$restrainingChains = new ArrayList<>();
    @Unique
    private boolean witchery$restrained = false;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

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
            LivingEntity self = (LivingEntity)(Object) this;
            self.zza = 0.0F;
            self.xxa = 0.0F;
            self.yya = 0.0F;
            self.setJumping(false);
        }
    }
}

