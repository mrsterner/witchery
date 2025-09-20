package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.sterner.witchery.api.event.SleepingEvent;
import dev.sterner.witchery.mixin_logic.PlayerMixinLogic;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    private int sleepCounter;

    @Inject(method = "stopSleepInBed", at = @At("TAIL"))
    private void witchery$stopSleepInBed(boolean wakeImmediately, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        Player player = Player.class.cast(this);
        SleepingEvent.Companion.getPOST().invoker().invoke(player, this.sleepCounter, wakeImmediately);
    }

    @ModifyReturnValue(method = "wantsToStopRiding", at = @At("RETURN"))
    private boolean witchery$neoStopDismount(boolean original) {
        var player = Player.class.cast(this);
        return PlayerMixinLogic.INSTANCE.wantsStopRiding(original, player);
    }
}
