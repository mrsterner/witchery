package dev.sterner.witchery.mixin.possession.possessed;

import dev.sterner.witchery.api.interfaces.ProtoPossessable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class PossessableEntityMixin implements ProtoPossessable {

    @Shadow
    private Level level;

    @Shadow
    public boolean hasImpulse;

    @Inject(method = "markHurt", at = @At("RETURN"))
    private void markHurt(CallbackInfo ci) {
        Player player = this.getPossessor();
        if (player != null && !level.isClientSide && this.hasImpulse) {
            player.hasImpulse = true;
        }
    }

    @Inject(method = "isControlledByLocalInstance", at = @At("HEAD"), cancellable = true)
    private void isControlledByLocalInstance(CallbackInfoReturnable<Boolean> cir) {
        if (this.isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Player player = this.getPossessor();
        if (player != null && player.isCreative()) {
            cir.setReturnValue(!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
        }
    }

    @Inject(method = "canUsePortal", at = @At("HEAD"), cancellable = true)
    private void canUsePortal(boolean allowPassengers, CallbackInfoReturnable<Boolean> cir) {
        if (this.isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("HEAD"), cancellable = true)
    private void startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        Player player = this.getPossessor();
        if (player != null) {
            cir.setReturnValue(player.startRiding(vehicle, force));
        }
    }

    @Inject(method = "refreshDimensions", at = @At("RETURN"))
    private void refreshPossessorDimensions(CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor != null) possessor.refreshDimensions();
    }

    @Inject(method = "saveAsPassenger", at = @At("HEAD"), cancellable = true)
    private void cancelPossessableSave(CompoundTag tag, CallbackInfoReturnable<Boolean> cir) {
        if (this.isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }
}