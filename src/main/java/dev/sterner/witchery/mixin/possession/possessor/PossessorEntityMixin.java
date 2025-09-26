package dev.sterner.witchery.mixin.possession.possessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class PossessorEntityMixin {

    @Invoker("isSprinting")
    public abstract boolean witchery$isSprinting();

    @Invoker("getX")
    protected abstract double witchery$getX();

    @Invoker("getY")
    protected abstract double witchery$getY();

    @Invoker("getZ")
    protected abstract double witchery$getZ();

    @Accessor("yRot")
    protected abstract float witchery$getYaw();

    @Accessor("xRot")
    public abstract float witchery$getPitch();

    @Accessor("horizontalCollision")
    protected abstract boolean witchery$isCollidingHorizontally();

    @Accessor("fallDistance")
    protected abstract float witchery$getFallDistance();

    @Accessor("fallDistance")
    public abstract void witchery$setFallDistance(float distance);

    @Accessor("level")
    protected abstract Level witchery$getWorld();

    @Accessor("stuckSpeedMultiplier")
    public abstract void witchery$setMovementMultiplier(Vec3 multiplier);

    @Invoker("checkFallDamage")
    protected abstract void witchery$fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition);

    @Inject(method = "getAirSupply", at = @At("HEAD"), cancellable = true)
    protected void witchery$delegateBreath(CallbackInfoReturnable<Integer> cir) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(method = "getMaxAirSupply", at = @At("HEAD"), cancellable = true)
    protected void witchery$delegateMaxBreath(CallbackInfoReturnable<Integer> cir) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(method = "isIgnoringBlockTriggers", at = @At("RETURN"), cancellable = true)
    protected void witchery$soulsAvoidTraps(CallbackInfoReturnable<Boolean> cir) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
    protected void witchery$isOnFire(CallbackInfoReturnable<Boolean> cir) {
        // overridden by PossessorPlayerEntityMixin
    }
}