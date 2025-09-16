package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import dev.sterner.witchery.api.EntityChainInterface;
import dev.sterner.witchery.api.interfaces.OnRemovedEffect;
import dev.sterner.witchery.entity.ChainEntity;
import dev.sterner.witchery.handler.BloodPoolHandler;
import dev.sterner.witchery.handler.NecroHandler;
import dev.sterner.witchery.handler.affliction.AfflictionTypes;
import dev.sterner.witchery.handler.affliction.TransformationHandler;
import dev.sterner.witchery.mixin_logic.LivingEntityMixinLogic;
import dev.sterner.witchery.platform.EtherealEntityAttachment;
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityChainInterface {

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

            if (data.getLevel(AfflictionTypes.LYCANTHROPY) >= 9) {
                if (TransformationHandler.isWolf(player) ||
                        TransformationHandler.isWerewolf(player)) {
                    return damage;
                }
            }
        }
        return damage;
    }
}

