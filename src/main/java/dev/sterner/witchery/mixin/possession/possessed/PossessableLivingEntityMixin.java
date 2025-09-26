package dev.sterner.witchery.mixin.possession.possessed;

import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.possession.EntityAiToggle;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
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
    @Unique
    private final boolean witchery$immovable = this.getType().is(WitcheryTags.INSTANCE.getIMMOVABLE());
    @Unique
    private final boolean witchery$regularEater = this.getType().is(WitcheryTags.INSTANCE.getREGULAR_EATER());
    @Nullable
    private UUID witchery$previousPossessorUuid;

    @Shadow public abstract float getHealth();
    @Shadow public abstract float getAbsorptionAmount();
    @Shadow public float yHeadRot;
    @Shadow public float yBodyRot;
    @Shadow @Nullable public abstract LivingEntity getLastHurtByMob();
    @Shadow
    public abstract boolean isUsingItem();
    @Shadow public abstract Brain<?> getBrain();
    @Shadow @Nullable public abstract AttributeInstance getAttribute(Holder<Attribute> attribute);

    @Nullable
    private Player possessor;

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
            PossessionComponentAttachment.INSTANCE.get(this.possessor).stopPossessing(!possessor.isCreative());
            this.setPossessor(null);
        }
        return possessor;
    }

    @Override
    public boolean canBePossessedBy(Player player) {
        return !this.isRemoved() && this.getHealth() > 0 && (this.possessor == null || this.possessor.getUUID().equals(player.getUUID()));
    }

    @Override
    public void setPossessor(@Nullable Player possessor) {
        if (possessor == this.possessor) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;

        if ((this.possessor != null && PossessionComponentAttachment.INSTANCE.get(this.possessor).getHost() == self) && !this.level().isClientSide) {
            throw new IllegalStateException("Players must stop possessing an entity before it can change possessor!");
        }

        if (possessor == null) {
            assert this.possessor != null;
            this.witchery$previousPossessorUuid = this.possessor.getUUID();
            this.fallDistance = this.possessor.fallDistance;
            this.setShiftKeyDown(false);
        }

        this.possessor = possessor;

        if (!this.level().isClientSide) {
            EntityAiToggle.toggleAi(self, EntityAiToggle.INSTANCE.getPOSSESSION_MECHANISM_ID(), this.possessor != null, false);
        }

        AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        // TODO: Add speed modifier constants and logic

        this.onPossessorSet(possessor);
    }

    @Override
    public boolean isRegularEater() {
        return witchery$regularEater;
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isImmobile()Z", ordinal = 0))
    private void witchery$mobTick(CallbackInfo ci) {
        if (this.isBeingPossessed() && !this.level().isClientSide) {
            this.witchery$mobTick();
        }
    }

    protected void witchery$mobTick() {
        // NO-OP
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        Player player = this.getPossessor();
        LivingEntity self = (LivingEntity) (Object) this;
        if (player != null) {
            if (!this.level().isClientSide) {
                if (self instanceof Monster && this.level().getDifficulty() == Difficulty.PEACEFUL) {
                    // TODO: Send message about peaceful despawn
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
            if (!this.witchery$immovable) {
                this.yBodyRot = this.yHeadRot;
                this.setSwimming(player.isSwimming());
                this.fallDistance = 0;

                this.setDeltaMovement(player.getDeltaMovement());
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setPos(player.getX(), player.getY(), player.getZ());

                this.horizontalCollision = player.horizontalCollision;
                this.verticalCollision = player.verticalCollision;
            }
        }
    }

    @Inject(method = {"doPush", "push"}, at = @At("HEAD"), cancellable = true)
    private void doPush(Entity entity, CallbackInfo ci) {
        if (entity == this.getPossessor()) {
            ci.cancel();
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer possessor = (ServerPlayer) this.getPossessor();
        if (possessor != null) {
            PossessionComponentAttachment.PossessionComponent component = PossessionComponentAttachment.INSTANCE.get(possessor);
            component.stopPossessing(!possessor.isCreative());

        }
    }

    @Inject(method = "updatingUsingItem", at = @At("HEAD"), cancellable = true)
    private void updateUsingItem(CallbackInfo ci) {
        if (this.isBeingPossessed()) {
            ci.cancel();
        }
    }

    @Inject(method = "onEffectAdded", at = @At("RETURN"))
    private void onEffectAdded(MobEffectInstance effectInstance, Entity entity, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor instanceof ServerPlayer) {
            possessor.addEffect(new MobEffectInstance(effectInstance));
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("RETURN"))
    private void onEffectUpdated(MobEffectInstance effectInstance, boolean reapplyEffect, Entity entity, CallbackInfo ci) {
        if (reapplyEffect) {
            Player possessor = this.getPossessor();
            if (possessor instanceof ServerPlayer) {
                possessor.addEffect(new MobEffectInstance(effectInstance));
            }
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("RETURN"))
    private void onEffectRemoved(MobEffectInstance effectInstance, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor instanceof ServerPlayer) {
            possessor.removeEffect(effectInstance.getEffect());
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
    private void teleportPossessor(double x, double y, double z, boolean particleEffects, CallbackInfoReturnable<Boolean> cir) {
        Player player = this.getPossessor();
        if (player != null) {
            cir.setReturnValue(player.randomTeleport(x, y, z, particleEffects));
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
    private void hurtCurrentlyUsedShield(float damage, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor != null && !this.level().isClientSide) {
            // TODO: Need accessor for protected method
            possessor.hurtCurrentlyUsedShield(damage);
            this.level().broadcastEntityEvent(possessor, (byte)29);
            ci.cancel();
        }
    }

    @Inject(method = "getUseItem", at = @At("HEAD"), cancellable = true)
    private void getUseItem(CallbackInfoReturnable<ItemStack> cir) {
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