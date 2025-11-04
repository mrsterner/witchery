package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PetrifiedMovementMixin {

    @Inject(
            method = "travel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventMovement(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (data.isPetrified()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "jumpFromGround",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventJumping(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (data.isPetrified()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "setSprinting",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventSprinting(boolean sprinting, CallbackInfo ci) {
        if (!sprinting) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (data.isPetrified()) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "knockback",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"
            )
    )
    private void reduceKnockback(LivingEntity instance, double x, double y, double z, Operation<Void> original) {
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(instance);

        if (data.isPetrified()) {
            original.call(instance, 0.0, 0.0, 0.0);
        } else {
            original.call(instance, x, y, z);
        }
    }

    @Inject(
            method = "tickHeadTurn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventTurning(float yRot, float animStep, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (data.isPetrified()) {
            cir.cancel();
        }
    }

    @Inject(
            method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventAttacking(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(entity);

        if (data.isPetrified()) {
            cir.setReturnValue(false);
        }
    }
}