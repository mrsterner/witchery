package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.api.SleepingEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow private int sleepCounter;

    @Inject(method = "stopSleepInBed", at = @At("TAIL"))
    private void stopSleepInBed(boolean wakeImmediately, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        SleepingEvent.Companion.getPOST().invoker().invoke(player, this.sleepCounter, wakeImmediately);
    }
}
