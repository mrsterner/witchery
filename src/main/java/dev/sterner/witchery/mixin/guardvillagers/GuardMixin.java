package dev.sterner.witchery.mixin.guardvillagers;

import dev.sterner.witchery.core.api.interfaces.VillagerTransfix;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.network.SpawnTransfixParticlesS2CPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tallestegg.guardvillagers.common.entities.Guard;

import java.util.UUID;

@Mixin(value = Guard.class)
public class GuardMixin extends PathfinderMob implements VillagerTransfix {
    @Unique
    int witchery$transfixCounter = 0;
    @Unique
    Vec3 witchery$transfixVector = null;

    @Unique
    UUID witchery$mesmerisedUUID = null;
    @Unique
    int witchery$mesmerisedUUIDCounter = 0;

    protected GuardMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void witchery$forceVampireAttack(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        if (entityIn instanceof Player player && AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0) {
            Guard guard = (Guard)(Object)this;

            float attackDamage = (float)guard.getAttributeValue(Attributes.ATTACK_DAMAGE);
            DamageSource damageSource = guard.damageSources().mobAttack(guard);
            boolean success = player.hurt(damageSource, attackDamage);

            if (success) {
                double knockbackStrength = guard.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                if (knockbackStrength > 0.0) {
                    player.knockback(
                            knockbackStrength * 0.5,
                            Math.sin(guard.getYRot() * ((float)Math.PI / 180F)),
                            -Math.cos(guard.getYRot() * ((float)Math.PI / 180F))
                    );
                }
            }

            cir.setReturnValue(success);
        }
    }
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void witchery$setTargetForVampires(LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            if (AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0) {
                super.setTarget(entity);
                ci.cancel();
            }
        }
    }

    @Unique
    private static boolean witchery$isVampHelper(LivingEntity target) {
        if (!(target instanceof Player player)) return false;
        return AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0;
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void witchery$registerGoals(CallbackInfo ci) {
        Guard guard = (Guard)(Object)this;
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                guard,
                Player.class,
                10,
                true,
                false,
                GuardMixin::witchery$isVampHelper
        ));
    }

    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void witchery$canAttackVampires(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player) {
            if (AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0) {
                cir.setReturnValue(true);
            }
        }
    }

    @Override
    public void setTransfixedLookVector(@NotNull Vec3 vec3) {
        witchery$transfixVector = vec3;
        witchery$transfixCounter = 20 * 10;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void witchery$onTick(CallbackInfo ci) {
        Guard guard = (Guard) (Object) this;

        if (!guard.level().isClientSide) {
            if (witchery$transfixCounter > 0) {
                if (witchery$mesmerisedUUIDCounter <= 0) {
                    guard.getNavigation().stop();
                    guard.getMoveControl().strafe(0.0F, 0.0F);
                }

                guard.getLookControl().setLookAt(witchery$transfixVector.x, witchery$transfixVector.y, witchery$transfixVector.z);

                PacketDistributor.sendToPlayersTrackingEntityAndSelf(guard, new SpawnTransfixParticlesS2CPayload(guard.position(), witchery$transfixCounter < 20));

                witchery$transfixCounter--;
            } else {
                witchery$transfixVector = null;
            }

            if (witchery$mesmerisedUUIDCounter > 0) {
                var player = guard.level().getPlayerByUUID(witchery$getMesmerized());
                if (player != null) {
                    guard.getNavigation().moveTo(player, 1.0F);
                }
                witchery$mesmerisedUUIDCounter--;
            } else {
                witchery$mesmerisedUUID = null;
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void witchery$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putInt("WitcheryTransfixCounter", witchery$transfixCounter);
        compoundTag.putInt("WitcheryMesmerisedUUIDCounter", witchery$mesmerisedUUIDCounter);
        if (witchery$transfixVector != null) {
            compoundTag.putDouble("WitcheryTransfixVectorX", witchery$transfixVector.x);
            compoundTag.putDouble("WitcheryTransfixVectorY", witchery$transfixVector.y);
            compoundTag.putDouble("WitcheryTransfixVectorZ", witchery$transfixVector.z);
        }
        if (witchery$mesmerisedUUID != null) {
            compoundTag.putUUID("WitcheryMesmerisedUUID", witchery$mesmerisedUUID);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void witchery$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        witchery$transfixCounter = compoundTag.getInt("WitcheryTransfixCounter");
        witchery$mesmerisedUUIDCounter = compoundTag.getInt("WitcheryMesmerisedUUIDCounter");
        if (compoundTag.contains("WitcheryTransfixVectorX")) {
            witchery$transfixVector =
                    new Vec3(
                            compoundTag.getDouble("WitcheryTransfixVectorX"),
                            compoundTag.getDouble("WitcheryTransfixVectorY"),
                            compoundTag.getDouble("WitcheryTransfixVectorZ")
                    );
        }
        if (compoundTag.contains("WitcheryMesmerisedUUID")) {
            witchery$mesmerisedUUID = compoundTag.getUUID("WitcheryMesmerisedUUID");
        }
    }

    @Override
    public boolean witchery$isTransfixed() {
        return witchery$transfixCounter > 0;
    }

    @Override
    public void witchery$setMesmerized(@NotNull UUID uuid) {
        this.witchery$mesmerisedUUID = uuid;
        this.witchery$mesmerisedUUIDCounter = 20 * 20;
    }

    @Override
    public boolean witchery$isMesmerized() {
        return witchery$mesmerisedUUID != null;
    }

    @NotNull
    @Override
    public UUID witchery$getMesmerized() {
        return witchery$mesmerisedUUID;
    }
}