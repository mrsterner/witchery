package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PossessorPlayerMixin extends PossessorLivingEntityMixin {

    @Shadow public abstract FoodData getFoodData();

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void travel(Vec3 movementInput, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        Mob possessed = PossessionManager.INSTANCE.getHost(self);
        if (possessed != null) {

        }
    }

    @Inject(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;jumpFromGround()V"))
    private void makeHostJump(CallbackInfo ci) {
        Player self = (Player)(Object)this;
        LivingEntity possessed = PossessionManager.INSTANCE.getHost(self);
        if (possessed != null) {
            possessed.jumpFromGround();
        }
    }

    @Inject(method = "updateSwimming", at = @At("RETURN"))
    private void cancelSwimming(CallbackInfo ci) {
        //TODO: MovementAlterer.KEY.get(this).updateSwimming();
    }

    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    private void adjustSize(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.getDimensions(pose));
        }
    }

    @Inject(method = "canEat", at = @At("RETURN"), cancellable = true)
    private void canSoulConsume(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        Mob possessed = PossessionManager.INSTANCE.getHost(self);
        if (possessed != null) {
            cir.setReturnValue(ignoreHunger || possessed.getType().is(WitcheryTags.INSTANCE.getREGULAR_EATER()) && this.getFoodData().needsFood());
        }
    }

    @Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
    private void addExhaustion(float exhaustion, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        Mob possessed = PossessionManager.INSTANCE.getHost(self);
        if (possessed != null && possessed.getType().is(WitcheryTags.INSTANCE.getREGULAR_EATER())) {
            if (!self.level().isClientSide) {
                self.getFoodData().addExhaustion(exhaustion);
            }
            ci.cancel();
        }
    }

    @Override
    protected void delegateBreath(CallbackInfoReturnable<Integer> cir) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.getAirSupply());
        }
    }

    @Override
    protected void delegateMaxBreath(CallbackInfoReturnable<Integer> cir) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.getMaxAirSupply());
        }
    }

    @Override
    protected void requiem$canFly(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            cir.setReturnValue(false);
        }
    }

    @Override
    protected void requiem$setSprinting(boolean sprinting, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            possessedEntity.setSprinting(sprinting);
        }
    }

    @Override
    protected void soulsAvoidTraps(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            Player self = (Player)(Object)this;
            if (AfflictionPlayerAttachment.getData(self).isSoulForm()) {
                cir.setReturnValue(true);
            } else {
                Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
                if (possessedEntity != null && possessedEntity.isIgnoringBlockTriggers()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Override
    protected void checkFire(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.isOnFire());
        } else if (AfflictionPlayerAttachment.getData(self).isSoulForm()) {
            cir.setReturnValue(false);
        }
    }


    @Override
    protected void requiem$canWalkOnFluid(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.canStandOnFluid(fluid));
        }
    }

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    private void slowMovement(BlockState state, Vec3 multiplier, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        Mob possessedEntity = PossessionManager.INSTANCE.getHost(self);
        if (possessedEntity != null) {
            possessedEntity.fallDistance = this.fallDistance;
            possessedEntity.makeStuckInBlock(state, multiplier);
            this.fallDistance = possessedEntity.fallDistance;
            this.setDeltaMovement(possessedEntity.getDeltaMovement());
            ci.cancel();
        }
    }
}