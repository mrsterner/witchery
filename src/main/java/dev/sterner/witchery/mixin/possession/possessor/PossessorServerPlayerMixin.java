package dev.sterner.witchery.mixin.possession.possessor;

import com.mojang.authlib.GameProfile;
import dev.sterner.witchery.features.possession.PossessionComponentAttachment;
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
public abstract class PossessorServerPlayerMixin extends Player {
    @Nullable
    private CompoundTag witchery$possessedEntityTag;

    public PossessorServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Unique
    public void witchery$setResurrectionEntity(Mob secondLife) {
        CompoundTag tag = new CompoundTag();
        if (witchery$saveAsPassenger(secondLife, tag)) {
            witchery$setResurrectionEntity(tag);
        }
    }

    @Unique
    public boolean witchery$hasResurrectionEntity() {
        return this.witchery$possessedEntityTag != null;
    }

    @Unique
    private void witchery$spawnResurrectionEntity() {
        if (witchery$possessedEntityTag == null) return;

        Entity formerPossessed = EntityType.loadEntityRecursive(
                this.witchery$possessedEntityTag,
                level(),
                Function.identity()
        );

        if (formerPossessed instanceof Mob host) {
            host.copyPosition(this);

            if (level().addFreshEntity(host)) {
                PossessionComponentAttachment.INSTANCE.get((ServerPlayer)(Object)this).startPossessing(host);
            }
        }

        this.witchery$possessedEntityTag = null;
    }



    @Unique
    private void witchery$setResurrectionEntity(@Nullable CompoundTag serializedSecondLife) {
        this.witchery$possessedEntityTag = serializedSecondLife;
    }

    @Inject(method = "changeDimension", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private void changePossessedDimension(DimensionTransition teleportTarget, CallbackInfoReturnable<Entity> cir) {
        witchery$prepareDimensionChange();
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private void changePossessedDimension(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        witchery$prepareDimensionChange();
    }

    @Unique
    private void witchery$prepareDimensionChange() {
        Mob currentHost = PossessionComponentAttachment.INSTANCE.get((ServerPlayer)(Object)this).getHost();
        if (currentHost != null && !currentHost.isRemoved()) {
            witchery$setResurrectionEntity(currentHost);
            PossessionComponentAttachment.INSTANCE.get((ServerPlayer)(Object)this).stopPossessing(false);
            currentHost.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }


    @Inject(method = "changeDimension", at = @At(value = "RETURN"))
    private void witchery$changeDimension(DimensionTransition teleportTarget, CallbackInfoReturnable<Entity> cir) {
        witchery$spawnResurrectionEntity();
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At(value = "RETURN"))
    private void witchery$teleportTo(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        witchery$spawnResurrectionEntity();
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void witchery$restoreFrom(ServerPlayer original, boolean fromEnd, CallbackInfo ci) {
        this.witchery$possessedEntityTag = ((PossessorServerPlayerMixin) (Object) original).witchery$possessedEntityTag;

        if (this.witchery$possessedEntityTag != null) {
            this.getInventory().replaceWith(original.getInventory());
        }
    }

    @Inject(method = "swing", at = @At("HEAD"))
    private void witchery$swing(InteractionHand hand, CallbackInfo ci) {
        LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessed != null) {
            possessed.swing(hand);
        }
    }

    @Inject(method = "onEffectAdded", at = @At("RETURN"))
    private void witchery$onEffectAdded(MobEffectInstance effect, Entity entity, CallbackInfo ci) {
        Mob possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessed != null) {
            possessed.addEffect(new MobEffectInstance(effect));
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("RETURN"))
    private void witchery$onEffectUpdated(MobEffectInstance effect, boolean upgrade, @Nullable Entity entity, CallbackInfo ci) {
        if (upgrade) {
            Mob possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

            if (possessed != null) {
                possessed.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("RETURN"))
    private void witchery$onEffectRemoved(MobEffectInstance effect, CallbackInfo ci) {
        Mob possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessed != null) {
            possessed.removeEffect(effect.getEffect());
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void witchery$addAdditionalSaveData(CompoundTag tag, CallbackInfo info) {
        Mob possessedEntity = PossessionComponentAttachment.INSTANCE.get(this).getHost();

        if (possessedEntity != null) {
            CompoundTag possessedRoot = new CompoundTag();
            CompoundTag serializedPossessed = new CompoundTag();

            witchery$saveAsPassenger(possessedEntity, serializedPossessed);
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

    @Unique
    public boolean witchery$saveAsPassenger(Mob possessedEntity, CompoundTag compound) {
        String s = possessedEntity.getEncodeId();
        if (s != null) {
            compound.putString("id", s);
            possessedEntity.saveWithoutId(compound);
            return true;
        } else {
            return false;
        }
    }
}