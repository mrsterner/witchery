package dev.sterner.witchery.mixin.possession.possessor;

import com.mojang.authlib.GameProfile;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.function.Function;

@Mixin(ServerPlayer.class)
public abstract class PossessorServerPlayerEntityMixin extends Player {
    @Nullable
    private CompoundTag witchery$possessedEntityTag;

    public PossessorServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    public void setResurrectionEntity(Mob secondLife) {
        CompoundTag tag = new CompoundTag();
        if (secondLife.saveAsPassenger(tag)) {
            setResurrectionEntity(tag);
        } else {
            // TODO: Add logging
        }
    }

    public boolean hasResurrectionEntity() {
        return this.witchery$possessedEntityTag != null;
    }

    public void spawnResurrectionEntity() {
        if (this.witchery$possessedEntityTag != null) {
            Entity formerPossessed = EntityType.loadEntityRecursive(
                    this.witchery$possessedEntityTag,
                    level(),
                    Function.identity()
            );

            if (formerPossessed instanceof Mob host) {
                host.copyPosition(this);
                if (level().addFreshEntity(host)) {
                    if (PossessionComponentAttachment.INSTANCE.get(this).startPossessing(host, false)) {
                        // TODO: Fire post resurrection event
                    }
                } else {
                    // TODO: Add logging
                }
            } else {
                // TODO: Add logging
            }

            this.witchery$possessedEntityTag = null;
        }
    }

    @Unique
    private void setResurrectionEntity(@Nullable CompoundTag serializedSecondLife) {
        this.witchery$possessedEntityTag = serializedSecondLife;
    }

    @Inject(method = "changeDimension", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private void changePossessedDimension(DimensionTransition teleportTarget, CallbackInfoReturnable<Entity> cir) {
        prepareDimensionChange();
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private void changePossessedDimension(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        prepareDimensionChange();
    }

    @Unique
    private void prepareDimensionChange() {
        Mob currentHost = PossessionComponentAttachment.INSTANCE.get(this).getHost();
        if (currentHost != null && !currentHost.isRemoved()) {
            this.setResurrectionEntity(currentHost);
            currentHost.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }

    @Inject(method = "changeDimension", at = @At(value = "RETURN"))
    private void onTeleportDone(DimensionTransition teleportTarget, CallbackInfoReturnable<Entity> cir) {
        spawnResurrectionEntity();
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At(value = "RETURN"))
    private void onTeleportDone(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        spawnResurrectionEntity();
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void clonePlayer(ServerPlayer original, boolean fromEnd, CallbackInfo ci) {
        this.witchery$possessedEntityTag = ((PossessorServerPlayerEntityMixin) (Object) original).witchery$possessedEntityTag;

        if (this.witchery$possessedEntityTag != null) {
            this.getInventory().replaceWith(original.getInventory());
        }
    }

    @Inject(method = "swing", at = @At("HEAD"))
    private void swingHand(InteractionHand hand, CallbackInfo ci) {
        LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessed != null) {
            possessed.swing(hand);
        }
    }

    @Inject(method = "onEffectAdded", at = @At("RETURN"))
    private void onStatusEffectAdded(MobEffectInstance effect, Entity entity, CallbackInfo ci) {
        Mob possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessed != null) {
            possessed.addEffect(new MobEffectInstance(effect));
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("RETURN"))
    private void onStatusEffectUpdated(MobEffectInstance effect, boolean upgrade, @Nullable Entity entity, CallbackInfo ci) {
        if (upgrade) {
            Mob possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

            if (possessed != null) {
                possessed.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("RETURN"))
    private void onStatusEffectRemoved(MobEffectInstance effect, CallbackInfo ci) {
        Mob possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessed != null) {
            possessed.removeEffect(effect.getEffect());
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writePossessedMobToTag(CompoundTag tag, CallbackInfo info) {
        Entity possessedEntity = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessedEntity != null) {
            Entity possessedEntityVehicle = possessedEntity.getRootVehicle();
            CompoundTag possessedRoot = new CompoundTag();
            CompoundTag serializedPossessed = new CompoundTag();
            possessedEntityVehicle.saveWithoutId(serializedPossessed);
            possessedRoot.put("PossessedEntity", serializedPossessed);
            possessedRoot.putUUID("PossessedUUID", possessedEntity.getUUID());
            tag.put("PossessedRoot", possessedRoot);
        } else if (this.witchery$possessedEntityTag != null) {
            CompoundTag possessedRoot = new CompoundTag();
            possessedRoot.put("PossessedEntity", this.witchery$possessedEntityTag);
            possessedRoot.putUUID("PossessedUUID", this.witchery$possessedEntityTag.getUUID("UUID"));
            tag.put("PossessedRoot", possessedRoot);
        }
    }
}