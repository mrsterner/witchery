package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
class ServerWorldMixin {
    @Shadow
    @Final
    private GameEventDispatcher gameEventDispatcher;

    @Inject(method = "gameEvent", at = @At("HEAD"))
    private void updatePossessorContext(Holder<GameEvent> event, Vec3 emitterPos, GameEvent.Context context, CallbackInfo ci) {
        if (context.sourceEntity() instanceof ServerPlayer player) {
            Mob host = PossessionManager.INSTANCE.getHost(player);
            if (host != null) {
                this.gameEventDispatcher.post(event, emitterPos, new GameEvent.Context(host, context.affectedState()));
            }
        }
    }
}