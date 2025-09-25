package dev.sterner.witchery.mixin.possession;


import com.mojang.authlib.GameProfile;
import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ServerPlayer.class)
abstract class PossessorServerPlayerMixin extends Player {

    @Unique
    @Nullable
    private CompoundTag requiem$possessedEntityTag;

    public PossessorServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "swing", at = @At("HEAD"))
    private void swingHand(InteractionHand hand, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        LivingEntity possessed = PossessionManager.INSTANCE.getHost(self);
        if (possessed != null) {
            possessed.swing(hand);
        }
    }

    @Inject(method = "onEffectAdded", at = @At("RETURN"))
    private void onStatusEffectAdded(MobEffectInstance effect, @Nullable Entity entity, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        Mob possessed = PossessionManager.INSTANCE.getHost(self);
        if (possessed != null) {
            possessed.addEffect(new MobEffectInstance(effect));
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("RETURN"))
    private void onStatusEffectUpdated(MobEffectInstance effect, boolean upgrade, @Nullable Entity entity, CallbackInfo ci) {
        if (upgrade) {
            ServerPlayer self = (ServerPlayer)(Object)this;
            Mob possessed = PossessionManager.INSTANCE.getHost(self);
            if (possessed != null) {
                possessed.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("RETURN"))
    private void onStatusEffectRemoved(MobEffectInstance effect, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        Mob possessed = PossessionManager.INSTANCE.getHost(self);
        if (possessed != null) {
            possessed.removeEffect(effect.getEffect());
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writePossessedMobToTag(CompoundTag tag, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);

        if (possessedEntity != null) {
            Entity possessedRoot = possessedEntity.getRootVehicle();
            CompoundTag possessedRootTag = new CompoundTag();
            CompoundTag serializedPossessed = new CompoundTag();
            possessedRoot.saveWithoutId(serializedPossessed);
            possessedRootTag.put("PossessedEntity", serializedPossessed);
            possessedRootTag.putUUID("PossessedUUID", possessedEntity.getUUID());
            tag.put("PossessedRoot", possessedRootTag);
        } else if (this.requiem$possessedEntityTag != null) {
            CompoundTag possessedRootTag = new CompoundTag();
            possessedRootTag.put("PossessedEntity", this.requiem$possessedEntityTag);
            possessedRootTag.putUUID("PossessedUUID", this.requiem$possessedEntityTag.getUUID("UUID"));
            tag.put("PossessedRoot", possessedRootTag);
        }
    }
}