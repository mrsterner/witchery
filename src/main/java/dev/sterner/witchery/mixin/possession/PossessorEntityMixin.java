package dev.sterner.witchery.mixin.possession;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class PossessorEntityMixin {
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow
    public abstract float getYRot();
    @Shadow public abstract float getXRot();
    @Shadow public abstract boolean isSprinting();
    @Shadow protected abstract Level level();
    @Shadow public boolean horizontalCollision;
    @Shadow public float fallDistance;
    @Shadow public abstract void setDeltaMovement(Vec3 vec);
    @Shadow public abstract Vec3 getDeltaMovement();
    @Shadow protected abstract void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition);

    @Inject(method = "getAirSupply", at = @At("HEAD"), cancellable = true)
    protected void delegateBreath(CallbackInfoReturnable<Integer> cir) {
        //TODO: Override in player mixin
    }

    @Inject(method = "getMaxAirSupply", at = @At("HEAD"), cancellable = true)
    protected void delegateMaxBreath(CallbackInfoReturnable<Integer> cir) {
        //TODO: Override in player mixin
    }

    @Inject(method = "isIgnoringBlockTriggers", at = @At("RETURN"), cancellable = true)
    protected void soulsAvoidTraps(CallbackInfoReturnable<Boolean> cir) {
        //TODO: Override in player mixin
    }

    @Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
    protected void checkFire(CallbackInfoReturnable<Boolean> cir) {
        //TODO: Override in player mixin
    }


}