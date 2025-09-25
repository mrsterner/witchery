package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.data_attachment.possession.DamageHelper;
import dev.sterner.witchery.data_attachment.possession.PossessionAttachment;
import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
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
    protected void requiem$canFly(CallbackInfoReturnable<Boolean> cir) {
        //Overidden
    }

    @Inject(method = "setSprinting", at = @At("RETURN"))
    protected void requiem$setSprinting(boolean sprinting, CallbackInfo ci) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    protected void requiem$canClimb(CallbackInfoReturnable<Boolean> cir) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    protected void requiem$canWalkOnFluid(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        // overridden by PossessorPlayerEntityMixin
    }

    @Inject(
            method = "checkFallDamage",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/LivingEntity;fallDistance:F", ordinal = 0),
            cancellable = true
    )
    private void onFall(double fallY, boolean onGround, BlockState floorBlock, BlockPos floorPos, CallbackInfo info) {
        if (this.level().isClientSide) return;

        Entity possessed = PossessionAttachment.INSTANCE.getHost((Entity) (Object) this);
        if (possessed != null && this.fallDistance > 0) {
            possessed.fallDistance = this.fallDistance;
            possessed.copyPosition((Entity) (Object) this);
            possessed.move(MoverType.SELF, Vec3.ZERO);
            this.checkFallDamage(fallY, onGround, floorBlock, floorPos);
        }
    }

    /**
     * Marks possessed entities as the attacker for any damage caused by their possessor.
     *
     * @param source damage dealt
     * @param amount amount of damage dealt
     */
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

}