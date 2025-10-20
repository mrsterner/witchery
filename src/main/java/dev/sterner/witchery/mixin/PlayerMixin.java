package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.sterner.witchery.core.api.event.SleepingEvent;
import dev.sterner.witchery.features.misc.InventoryLockPlayerAttachment;
import dev.sterner.witchery.mixin_logic.PlayerMixinLogic;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    private int sleepCounter;

    @Inject(method = "stopSleepInBed", at = @At("TAIL"))
    private void witchery$stopSleepInBed(boolean wakeImmediately, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        Player player = Player.class.cast(this);
        NeoForge.EVENT_BUS.post(new SleepingEvent.Stop(player, this.sleepCounter, wakeImmediately));
    }

    @ModifyReturnValue(method = "wantsToStopRiding", at = @At("RETURN"))
    private boolean witchery$neoStopDismount(boolean original) {
        var player = Player.class.cast(this);
        return PlayerMixinLogic.INSTANCE.wantsStopRiding(original, player);
    }

    @Inject(method = "canUseGameMasterBlocks", at = @At("HEAD"), cancellable = true)
    private void witchery$preventBlockPlacementFromLockedSlot(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        if (InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(self, self.getInventory().selected)) {
            cir.setReturnValue(false);
        }
    }
}
