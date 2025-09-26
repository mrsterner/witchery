package dev.sterner.witchery.mixin.possession.possessor;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;

@Mixin(Player.class)
public abstract class PossessorPlayerMixin extends PossessorLivingEntityMixin {

    @Shadow
    public abstract FoodData getFoodData();

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void witchery$travel(Vec3 travelVector, CallbackInfo info) {
        Entity possessed = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessed != null && ((Possessable) possessed).isRegularEater()) {
            if (!this.witchery$getWorld().isClientSide && (this.witchery$getX() != possessed.getX() || this.witchery$getY() != possessed.getY() || this.witchery$getZ() != possessed.getZ())) {
                ServerGamePacketListenerImpl networkHandler = ((ServerPlayer) (Object) this).connection;
                networkHandler.teleport(possessed.getX(), possessed.getY(), possessed.getZ(), this.witchery$getYaw(), this.witchery$getPitch(), EnumSet.allOf(RelativeMovement.class));
                networkHandler.resetPosition();
            }
            info.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void witchery$tick(CallbackInfo ci) {
        Player self = (Player)(Object)this;
        Entity possessed = PossessionComponentAttachment.INSTANCE.get(self).getHost();

        if (possessed != null) {
            EntityDimensions possessedDims = possessed.getDimensions(self.getPose());
            EntityDimensions currentDims = self.getDimensions(self.getPose());

            if (Math.abs(possessedDims.height() - currentDims.height()) > 0.01f ||
                    Math.abs(possessedDims.width() - currentDims.width()) > 0.01f) {
                self.refreshDimensions();
            }
        }
    }

    @Inject(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;jumpFromGround()V"))
    private void witchery$jumpFromGround(CallbackInfo ci) {
        LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessed != null) {
            possessed.jumpFromGround();
        }
    }

    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    private void witchery$getDefaultDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        Entity possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.getDimensions(pose));
        }
    }

    @Inject(method = "canEat", at = @At("RETURN"), cancellable = true)
    private void witchery$canEat(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        Possessable possessed = (Possessable) PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessed != null) {
            cir.setReturnValue(ignoreHunger || possessed.isRegularEater() && this.getFoodData().needsFood());
        }
    }

    @Inject(method = "isHurt", at = @At("RETURN"), cancellable = true)
    private void witchery$isHurt(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessed != null) {
            cir.setReturnValue(((Possessable) possessed).isRegularEater() && possessed.getHealth() > 0 && possessed.getHealth() < possessed.getMaxHealth());
        }
    }

    @WrapWithCondition(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private boolean witchery$causeFoodExhaustion(FoodData instance, float exhaustion) {
        Possessable possessed = (Possessable) PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessed != null && possessed.isRegularEater()) {
            if (!this.witchery$getWorld().isClientSide) {
                this.getFoodData().addExhaustion(exhaustion);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void witchery$delegateBreath(CallbackInfoReturnable<Integer> cir) {
        Entity possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.getAirSupply());
        }
    }

    @Override
    protected void witchery$delegateMaxBreath(CallbackInfoReturnable<Integer> cir) {
        Entity possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.getMaxAirSupply());
        }
    }

    @Override
    protected void witchery$canFly(CallbackInfoReturnable<Boolean> cir) {
        Mob possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            cir.setReturnValue(false);
        }
    }

    @Override
    protected void witchery$setSprinting(boolean sprinting, CallbackInfo ci) {
        Mob possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            possessedEntity.setSprinting(sprinting);
        }
    }

    @Override
    protected void witchery$canClimb(CallbackInfoReturnable<Boolean> cir) {

    }

    @Override
    protected void witchery$soulsAvoidTraps(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        if (!cir.getReturnValueZ()) {
            if (AfflictionPlayerAttachment.getData(self).isSoulForm()) {
                cir.setReturnValue(true);
            } else {
                Mob possessedEntity = PossessionComponentAttachment.INSTANCE.get(self).getHost();
                if (possessedEntity != null && possessedEntity.isIgnoringBlockTriggers()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Override
    protected void witchery$isOnFire(CallbackInfoReturnable<Boolean> cir) {
        Mob possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.isOnFire());
        } else if (AfflictionPlayerAttachment.getData((Player)(Object)this).isSoulForm()) {
            cir.setReturnValue(false);
        }
    }

    @Override
    protected void witchery$canWalkOnFluid(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        Mob possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            cir.setReturnValue(possessedEntity.canStandOnFluid(fluid));
        }
    }

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    private void witchery$makeStuckInBlock(BlockState state, Vec3 motionMultiplier, CallbackInfo ci) {
        Mob possessedEntity = PossessionComponentAttachment.INSTANCE.get((Player)(Object)this).getHost();
        if (possessedEntity != null) {
            possessedEntity.fallDistance = this.witchery$getFallDistance();
            possessedEntity.makeStuckInBlock(state, motionMultiplier);
            this.witchery$setFallDistance(possessedEntity.fallDistance);
            this.witchery$setMovementMultiplier(((EntityAccessor) possessedEntity).witchery$getMovementMultiplier());
            ci.cancel();
        }
    }
}