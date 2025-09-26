package dev.sterner.witchery.mixin.possession.possessed;

import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.api.interfaces.ProtoPossessable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class PossessableEntityMixin implements ProtoPossessable {

    @Shadow
    private Level level;

    @Shadow
    public boolean hasImpulse;

    /**
     * Nullable possessor, only present if this entity is actually possessed.
     */
    @Unique
    @Nullable
    private Player witchery$possessor;

    /**
     * Implement ProtoPossessable
     */
    @Override
    public boolean isBeingPossessed() {
        return this instanceof Possessable && this.witchery$possessor != null;
    }

    @Nullable
    @Override
    public Player getPossessor() {
        if (!isBeingPossessed()) return null;

        // Remove possessor if they were removed from the world
        if (this.witchery$possessor != null && this.witchery$possessor.isRemoved()) {
            this.witchery$possessor = null;
        }
        return this.witchery$possessor;
    }

    @ApiStatus.Internal
    public void setPossessor(@Nullable Player possessor) {
        this.witchery$possessor = possessor;
    }

    // --- Mixin injections ---

    @Inject(method = "markHurt", at = @At("RETURN"))
    private void witchery$markHurt(CallbackInfo ci) {
        Player player = this.getPossessor();
        if (player != null && !level.isClientSide && this.hasImpulse) {
            player.hasImpulse = true;
        }
    }

    @Inject(method = "isControlledByLocalInstance", at = @At("HEAD"), cancellable = true)
    private void witchery$isControlledByLocalInstance(CallbackInfoReturnable<Boolean> cir) {
        if (isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void witchery$isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Player player = getPossessor();
        if (player != null && player.isCreative()) {
            cir.setReturnValue(!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
        }
    }

    @Inject(method = "canUsePortal", at = @At("HEAD"), cancellable = true)
    private void witchery$canUsePortal(boolean allowPassengers, CallbackInfoReturnable<Boolean> cir) {
        if (isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("HEAD"), cancellable = true)
    private void witchery$startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        Player player = getPossessor();
        if (player != null) {
            cir.setReturnValue(player.startRiding(vehicle, force));
        }
    }

    @Inject(method = "refreshDimensions", at = @At("RETURN"))
    private void witchery$refreshDimensions(CallbackInfo ci) {
        Player possessor = getPossessor();
        if (possessor != null) possessor.refreshDimensions();
    }

    @Inject(method = "saveAsPassenger", at = @At("HEAD"), cancellable = true)
    private void witchery$saveAsPassenger(CompoundTag tag, CallbackInfoReturnable<Boolean> cir) {
        if (isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }
}
