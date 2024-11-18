package dev.sterner.witchery.mixin;

import dev.sterner.witchery.data.BloodPoolHandler;
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob mob = Mob.class.cast(this);

        var data = BloodPoolLivingEntityAttachment.getData(mob);
        var bloodJson = BloodPoolHandler.INSTANCE.getBLOOD_PAIR();
        if (data.getMaxBlood() == 0 && data.getBloodPool() == 0) {
            var entityType = mob.getType();
            var bloodValue = bloodJson.get(entityType);

            if (bloodValue != null) {
                var maxBlood = bloodValue.getBloodDrops() * 300;
                BloodPoolLivingEntityAttachment.setData(mob, new BloodPoolLivingEntityAttachment.Data(maxBlood, maxBlood));
            }
        }
    }
}
