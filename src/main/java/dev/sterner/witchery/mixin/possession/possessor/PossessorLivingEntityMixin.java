package dev.sterner.witchery.mixin.possession.possessor;

import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.data_attachment.possession.movement.MovementAltererAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import dev.sterner.witchery.util.DamageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PossessorLivingEntityMixin extends PossessorEntityMixin {

    @Shadow
    public abstract void setSprinting(boolean sprinting);

    @ModifyArg(method = "jumpInLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), index = 1)
    private double updateSwimVelocity(double upwardsVelocity) {
        MovementAltererAttachment.MovementAlterer alterer = MovementAltererAttachment.INSTANCE.get((Player)(Object)this);
        if (alterer != null) {
            return alterer.getSwimmingUpwardsVelocity(upwardsVelocity);
        }
        return upwardsVelocity;
    }

    @ModifyVariable(
            method = "travel",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private float fixUnderwaterVelocity(float speedAmount) {
        MovementAltererAttachment.MovementAlterer alterer = MovementAltererAttachment.INSTANCE.get((Player)(Object)this);
        if (alterer != null) {
            return alterer.getSwimmingAcceleration(speedAmount);
        }
        return speedAmount;
    }

    @Inject(method = "isFallFlying", at = @At("RETURN"), cancellable = true)
    protected void witchery$canFly(CallbackInfoReturnable<Boolean> cir) {
        // Overridden
    }

    @Inject(method = "setSprinting", at = @At("RETURN"))
    protected void witchery$setSprinting(boolean sprinting, CallbackInfo ci) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    protected void witchery$canClimb(CallbackInfoReturnable<Boolean> cir) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    protected void witchery$canWalkOnFluid(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(
            method = "checkFallDamage",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/LivingEntity;fallDistance:F", ordinal = 0),
            cancellable = true
    )
    private void onFall(double fallY, boolean onGround, BlockState floorBlock, BlockPos floorPos, CallbackInfo info) {
        if (this.witchery$getWorld().isClientSide) return;

        LivingEntity self = (LivingEntity)(Object)this;
        if (self instanceof Player player) {
            Entity possessed = PossessionComponentAttachment.INSTANCE.get(player).getHost();
            if (possessed != null && this.witchery$getFallDistance() > 0) {
                possessed.fallDistance = this.witchery$getFallDistance();
                possessed.copyPosition((Entity) (Object) this);
                possessed.move(MoverType.SELF, Vec3.ZERO);
                ((PossessorLivingEntityMixin) (Object) possessed).witchery$fall(fallY, onGround, floorBlock, floorPos);
            }
        }

    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private DamageSource proxyDamage(DamageSource source, DamageSource s, float amount) {
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity) {
            DamageSource newSource = DamageHelper.INSTANCE.tryProxyDamage(source, (LivingEntity) attacker);
            if (newSource != null) {
                return newSource;
            }
        }
        return source;
    }

    private boolean witchery$wasSprinting;

    @Inject(method = "getFluidFallingAdjustedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"))
    private void preventWaterHovering(double gravity, boolean isFalling, Vec3 deltaMovement, CallbackInfoReturnable<Vec3> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self instanceof Player player && this.witchery$isSprinting() && MovementAltererAttachment.INSTANCE.get(player).disablesSwimming()) {
            witchery$wasSprinting = true;
            this.setSprinting(false);
        }
    }

    @Inject(method = "getFluidFallingAdjustedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z", shift = At.Shift.AFTER))
    private void restoreSprint(double gravity, boolean isFalling, Vec3 deltaMovement, CallbackInfoReturnable<Vec3> cir) {
        if (witchery$wasSprinting) {
            witchery$wasSprinting = false;
            this.setSprinting(true);
        }
    }

    @Inject(method = "startSleeping", at = @At("RETURN"))
    private void makeHostSleep(BlockPos pos, CallbackInfo ci) {
        LivingEntity host = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (host != null && host.getType().is(WitcheryTags.INSTANCE.getSLEEPERS())) {
            host.startSleeping(pos);
        }
    }

    @Inject(method = "stopSleeping", at = @At("RETURN"))
    private void makeHostWakeUp(CallbackInfo ci) {
        LivingEntity host = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (host != null && host.getType().is(WitcheryTags.INSTANCE.getSLEEPERS())) {
            host.stopSleeping();
        }
    }
}