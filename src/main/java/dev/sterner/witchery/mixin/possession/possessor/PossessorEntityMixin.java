package dev.sterner.witchery.mixin.possession.possessor;

import dev.sterner.witchery.features.possession.PossessionComponentAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
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

    }

    @Inject(method = "getMaxAirSupply", at = @At("HEAD"), cancellable = true)
    protected void witchery$delegateMaxBreath(CallbackInfoReturnable<Integer> cir) {

    }

    @Inject(method = "isIgnoringBlockTriggers", at = @At("RETURN"), cancellable = true)
    protected void witchery$soulsAvoidTraps(CallbackInfoReturnable<Boolean> cir) {

    }

    @Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
    protected void witchery$isOnFire(CallbackInfoReturnable<Boolean> cir) {

    }

    @Inject(method = "getEyeHeight(Lnet/minecraft/world/entity/Pose;)F", at = @At("HEAD"), cancellable = true)
    private void witchery$adjustEyeHeight(Pose pose, CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player) {
            Entity possessedEntity = PossessionComponentAttachment.INSTANCE.get(player).getHost();
            if (possessedEntity instanceof LivingEntity livingEntity) {
                cir.setReturnValue((livingEntity).getEyeHeight(pose));
            }
        }

    }
}