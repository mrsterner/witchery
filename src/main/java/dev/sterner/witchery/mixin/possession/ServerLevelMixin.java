package dev.sterner.witchery.mixin.possession;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
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

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Shadow
    @Final
    private GameEventDispatcher gameEventDispatcher;

    @WrapWithCondition(method = "gameEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gameevent/GameEventDispatcher;post(Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private boolean updatePossessorContext(GameEventDispatcher instance, Holder<GameEvent> event, Vec3 emitterPos, GameEvent.Context emitter) {
        if (emitter.sourceEntity() instanceof ServerPlayer player) {
            Mob host = PossessionComponentAttachment.INSTANCE.get(player).getHost();
            if (host != null) {
                this.gameEventDispatcher.post(event, emitterPos, GameEvent.Context.of(host, emitter.affectedState()));
            }
        }
        return true;
    }
}