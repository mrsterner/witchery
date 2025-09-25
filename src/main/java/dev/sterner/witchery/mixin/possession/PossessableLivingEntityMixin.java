package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class PossessableLivingEntityMixin extends Entity implements Possessable {

    @Shadow
    public abstract float getHealth();
    @Shadow public abstract float getAbsorptionAmount();
    @Shadow protected float yHeadRot;
    @Shadow public float yBodyRot;
    @Shadow @Nullable
    public abstract LivingEntity getLastHurtMob();
    @Shadow public abstract boolean isUsingItem();
    @Shadow public abstract AttributeInstance getAttribute(net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute);

    @Unique
    @Nullable
    private Player possessor;

    @Unique
    @Nullable
    private UUID requiem$previousPossessorUuid;

    public PossessableLivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean isBeingPossessed() {
        return this.possessor != null;
    }

    @Nullable
    @Override
    public Player getPossessor() {
        if (this.possessor != null && this.possessor.isRemoved()) {
            PossessionManager.INSTANCE.stopPossessing(this.possessor, true);
            this.setPossessor(null);
        }
        return possessor;
    }

    @Override
    public boolean canBePossessedBy(Player player) {
        return !this.isRemoved() && this.getHealth() > 0 &&
                (this.possessor == null || this.possessor.getUUID().equals(player.getUUID()));
    }

    @Override
    public void setPossessor(@Nullable Player possessor) {
        if (possessor == this.possessor) {
            return;
        }

        if (possessor == null && this.possessor != null) {
            this.requiem$previousPossessorUuid = this.possessor.getUUID();
            this.fallDistance = this.possessor.fallDistance;
            this.setShiftKeyDown(false);
        }

        this.possessor = possessor;

        if (!this.level().isClientSide) {
            EntityAiToggle.get((LivingEntity) (Object) this).toggleAi(RequiemCore.POSSESSION_MECHANISM_ID, this.possessor != null, false);
        }

        AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(PossessionManager.INSTANCE.getINHERENT_MOB_SLOWNESS_UUID());
            if (possessor != null) {
                speedAttribute.addTransientModifier(PossessionManager.INSTANCE.getINHERENT_MOB_SLOWNESS());
            }
        }

        this.onPossessorSet(possessor);
    }

    @Override
    public boolean isRegularEater() {
        return this.getType().is(WitcheryTags.INSTANCE.getREGULAR_EATER());
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isControlledByLocalInstance()Z", ordinal = 0))
    private void requiem$mobTick(CallbackInfo ci) {
        if (this.isBeingPossessed() && !this.level().isClientSide) {
            this.requiem$mobTick();
        }
    }

    protected void requiem$mobTick() {
        // NO-OP
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        Player player = this.getPossessor();
        if (player != null) {
            if (!this.level().isClientSide) {
                if (this.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD) &&
                        this.level().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.translatable("requiem.message.peaceful_despawn"),
                            true
                    );
                }
                player.setAbsorptionAmount(this.getAbsorptionAmount());
            }
            this.setOnGround(player.onGround());
            this.setShiftKeyDown(player.isShiftKeyDown());
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.AFTER))
    private void afterTravel(CallbackInfo ci) {
        Player player = this.getPossessor();
        if (player != null) {
            this.setYRot(player.getYRot());
            this.setXRot(player.getXRot());
            this.yHeadRot = this.yRotO = this.getYRot();

            //TODO: if (!((VariableMobilityEntity)this).requiem_isImmovable()) {
            this.yBodyRot = this.yHeadRot;
            this.setSwimming(player.isSwimming());
            this.fallDistance = 0;

            this.setDeltaMovement(player.getDeltaMovement());
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setPos(player.getX(), player.getY(), player.getZ());

            this.horizontalCollision = player.horizontalCollision;
            this.verticalCollision = player.verticalCollision;
            //TODO: }
        }
    }

    @Inject(method = {"push(Lnet/minecraft/world/entity/Entity;)V", "doPush"}, at = @At("HEAD"), cancellable = true)
    private void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if (entity == this.getPossessor()) {
            ci.cancel();
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void onDeath(DamageSource deathCause, CallbackInfo ci) {
        ServerPlayer possessor = (ServerPlayer) this.getPossessor();
        LivingEntity self = (LivingEntity)(Object)this;
        if (possessor != null && self instanceof Mob mob) {
            //TODO: PossessionEvents.HOST_DEATH.invoker().onHostDeath(possessor, (LivingEntity)(Object)this, deathCause);
            PossessionManager.INSTANCE.handleHostDeath(possessor, mob, deathCause);
        }
    }

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void updateHeldItem(CallbackInfo ci) {
        if (this.isBeingPossessed()) {
            ci.cancel();
        }
    }

    @Inject(method = "onEffectAdded", at = @At("RETURN"))
    private void onStatusEffectAdded(MobEffectInstance effect, Entity entity, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor instanceof ServerPlayer) {
            possessor.addEffect(new MobEffectInstance(effect));
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("RETURN"))
    private void onStatusEffectUpdated(MobEffectInstance effect, boolean reapplyEffect, Entity entity, CallbackInfo ci) {
        if (reapplyEffect) {
            Player possessor = this.getPossessor();
            if (possessor instanceof ServerPlayer) {
                possessor.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("RETURN"))
    private void onStatusEffectRemoved(MobEffectInstance effect, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor instanceof ServerPlayer) {
            possessor.removeEffect(effect.getEffect());
        }
    }

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void knockback(double strength, double x, double z, CallbackInfo ci) {
        Player possessing = getPossessor();
        if (possessing != null) {
            possessing.knockback(strength, x, z);
            ci.cancel();
        }
    }

    @Inject(method = "randomTeleport", at = @At("HEAD"), cancellable = true)
    private void teleportPossessor(double x, double y, double z, boolean broadcastTeleport, CallbackInfoReturnable<Boolean> cir) {
        Player player = this.getPossessor();
        if (player != null) {
            cir.setReturnValue(player.randomTeleport(x, y, z, broadcastTeleport));
        }
    }

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    private void isFallFlying(CallbackInfoReturnable<Boolean> cir) {
        Player player = this.getPossessor();
        if (player != null) {
            cir.setReturnValue(player.isFallFlying());
        }
    }

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void isBlocking(CallbackInfoReturnable<Boolean> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.isBlocking());
        }
    }

    @Inject(method = "hurtCurrentlyUsedShield", at = @At("HEAD"), cancellable = true)
    private void damageShield(float damage, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor != null && !this.level().isClientSide) {
            possessor.hurtCurrentlyUsedShield(damage);
            this.level().broadcastEntityEvent(possessor, (byte)29);
            ci.cancel();
        }
    }

    @Inject(method = "getUseItem", at = @At("HEAD"), cancellable = true)
    private void getActiveItem(CallbackInfoReturnable<ItemStack> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.getUseItem());
        }
    }

    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    private void isUsingItem(CallbackInfoReturnable<Boolean> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.isUsingItem());
        }
    }
}