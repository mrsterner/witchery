package dev.sterner.witchery.mixin;

import dev.sterner.witchery.entity.SleepingPlayerEntity;
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private void witchery$respawnAtSleeping(boolean keepInventory, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir){
        var player = ServerPlayer.class.cast(this);

        for (ServerLevel serverLevel: player.level().getServer().getAllLevels()) {
            var hasSleeping = SleepingPlayerLevelAttachment.INSTANCE.getPlayerFromSleeping(player.getUUID(), serverLevel);
            if (hasSleeping != null) {
                var sleepEntity = serverLevel.getEntity(hasSleeping);
                if (sleepEntity instanceof SleepingPlayerEntity sleepingPlayerEntity) {
                    var pos = sleepingPlayerEntity.blockPosition();
                    var tran = new DimensionTransition(serverLevel, pos.getCenter(), Vec3.ZERO, sleepingPlayerEntity.getYRot(), 0.0F, postDimensionTransition);
                    cir.setReturnValue(tran);
                }
            }
        }
    }
}
