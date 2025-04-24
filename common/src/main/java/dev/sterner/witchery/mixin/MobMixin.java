package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.MobHelper;
import dev.sterner.witchery.data.BloodPoolHandler;
import dev.sterner.witchery.entity.goal.DisorientationGoal;
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment;
import dev.sterner.witchery.util.WitcheryConstants;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements MobAccessor {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob mob = Mob.class.cast(this);

        var data = BloodPoolLivingEntityAttachment.getData(mob);
        var bloodJson = BloodPoolHandler.INSTANCE.getBLOOD_PAIR();
        if (data.getMaxBlood() == 0 && data.getBloodPool() == 0) {
            var entityType = mob.getType();
            var bloodValue = bloodJson.get(entityType);

            if (bloodValue != null) {
                var maxBlood = bloodValue.getBloodDrops() * WitcheryConstants.BLOOD_DROP;
                BloodPoolLivingEntityAttachment.setData(mob, new BloodPoolLivingEntityAttachment.Data(maxBlood, maxBlood));
            }
        }
    }

    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Unique
    private int disorientCooldown = 0;
    @Unique
    private int disorientTime = 0;

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void registerDisorientationGoal(CallbackInfo ci) {
        goalSelector.addGoal(0, new DisorientationGoal((Mob)(Object)this));
    }

    @Inject(method = "defineSynchedData", at = @At("HEAD"))
    public void defineDisorientData(CallbackInfo ci, @Local(argsOnly = true) SynchedEntityData.Builder builder) {
        builder.define(MobHelper.DISORIENTED, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickDisorientation(CallbackInfo ci) {
        if (this.entityData.get(MobHelper.DISORIENTED)) {
            disorientTime++;
            if (disorientTime >= 20 * 20) {
                this.entityData.set(MobHelper.DISORIENTED, false);
                disorientCooldown = 20 * 20;
                disorientTime = 0;
            }
        } else if (disorientCooldown > 0) {
            disorientCooldown--;
        }
    }

    @Override
    public boolean witchery$canBeDisoriented() {
        return disorientCooldown <= 0 && !this.entityData.get(MobHelper.DISORIENTED);
    }

    @Override
    public void witchery$setDisorientedActive(boolean active) {
        if (witchery$canBeDisoriented()) {
            this.entityData.set(MobHelper.DISORIENTED, active);
            disorientTime = 0;
        }
    }
}
