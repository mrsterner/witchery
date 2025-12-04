package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity  {
    @Shadow @Final private Abilities abilities;

    @Unique
    private static final EntityDimensions SOUL_SNEAKING_SIZE = EntityDimensions.scalable(0.6f, 0.6f);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
    private void witchery$isSoulFormSwimming(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        if (this.abilities.flying && this.isSprinting() && AfflictionPlayerAttachment.getData(self).isSoulForm()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getMovementEmission", at = @At("RETURN"), cancellable = true)
    private void witchery$preventMoveEffects(CallbackInfoReturnable<MovementEmission> cir) {
        Player self = (Player)(Object)this;
        if (cir.getReturnValue() != MovementEmission.NONE && AfflictionPlayerAttachment.getData(self).isSoulForm()) {
            cir.setReturnValue(MovementEmission.NONE);
        }
    }


    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
    private void witchery$flySwimVertically(Vec3 motion, CallbackInfo ci) {
        Vec3 look = this.getLookAngle();
        double yMotion = look.y;
        double modifier = yMotion < -0.2D ? 0.085D : 0.06D;
        Player self = (Player)(Object)this;
        if (yMotion > 0.0D && !this.jumping && this.level().getBlockState(BlockPos.containing(
                this.getX(),
                this.getY() + 1.0D - 0.1D,
                this.getZ()
        )).getFluidState().isEmpty() && AfflictionPlayerAttachment.getData(self).isSoulForm()) {
            Vec3 velocity = this.getDeltaMovement();
            this.setDeltaMovement(velocity.add(0.0D, (yMotion - velocity.y) * modifier, 0.0D));
        }
    }

    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    private void witchery$adjustSize(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        Player self = (Player)(Object)this;
        if (AfflictionPlayerAttachment.getData(self).isSoulForm() && pose == Pose.CROUCHING) {
            cir.setReturnValue(SOUL_SNEAKING_SIZE);
        }
    }
}
