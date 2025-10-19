package dev.sterner.witchery.mixin.possession.possessor;

import dev.sterner.witchery.core.registry.WitcheryTags;
import dev.sterner.witchery.core.util.DamageHelper;
import dev.sterner.witchery.features.possession.PossessionComponentAttachment;
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

    @Inject(method = "isFallFlying", at = @At("RETURN"), cancellable = true)
    protected void witchery$canFly(CallbackInfoReturnable<Boolean> cir) {

    }

    @Inject(method = "setSprinting", at = @At("RETURN"))
    protected void witchery$setSprinting(boolean sprinting, CallbackInfo ci) {

    }

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    protected void witchery$canClimb(CallbackInfoReturnable<Boolean> cir) {

    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    protected void witchery$canWalkOnFluid(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {

    }

    @Inject(
            method = "checkFallDamage",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/LivingEntity;fallDistance:F", ordinal = 0),
            cancellable = true
    )
    private void witchery$checkFallDamage(double fallY, boolean onGround, BlockState floorBlock, BlockPos floorPos, CallbackInfo info) {
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
    private DamageSource witchery$hurt(DamageSource source, DamageSource s, float amount) {
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
    private void witchery$getFluidFallingAdjustedMovement(double gravity, boolean isFalling, Vec3 deltaMovement, CallbackInfoReturnable<Vec3> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self instanceof Player player) {
            this.witchery$isSprinting();
        }
    }

    @Inject(method = "getFluidFallingAdjustedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z", shift = At.Shift.AFTER))
    private void witchery$getFluidFallingAdjustedMovement2(double gravity, boolean isFalling, Vec3 deltaMovement, CallbackInfoReturnable<Vec3> cir) {
        if (witchery$wasSprinting) {
            witchery$wasSprinting = false;
            this.setSprinting(true);
        }
    }

    @Inject(method = "startSleeping", at = @At("RETURN"))
    private void witchery$startSleeping(BlockPos pos, CallbackInfo ci) {
        LivingEntity host = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (host != null && host.getType().is(WitcheryTags.INSTANCE.getSLEEPERS())) {
            host.startSleeping(pos);
        }
    }

    @Inject(method = "stopSleeping", at = @At("RETURN"))
    private void witchery$stopSleeping(CallbackInfo ci) {
        LivingEntity host = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (host != null && host.getType().is(WitcheryTags.INSTANCE.getSLEEPERS())) {
            host.stopSleeping();
        }
    }
}