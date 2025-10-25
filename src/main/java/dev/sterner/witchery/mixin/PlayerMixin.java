package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.sterner.witchery.core.api.event.SleepingEvent;
import dev.sterner.witchery.features.death.DeathPlayerAttachment;
import dev.sterner.witchery.features.death.DeathTransformationHelper;
import dev.sterner.witchery.features.misc.InventoryLockPlayerAttachment;
import dev.sterner.witchery.features.misc.MiscPlayerAttachment;
import dev.sterner.witchery.mixin_logic.PlayerMixinLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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

    @Inject(method = "tick", at = @At("TAIL"))
    private void witchery$deathNightVision(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (!player.level().isClientSide) {
            if (DeathTransformationHelper.INSTANCE.hasDeathHood(player)) {
                var data = DeathPlayerAttachment.getData(player);
                if (data.getHasDeathNightVision()) {
                    boolean isDark = player.level().dimensionType().ambientLight() == 0.0f ||
                            player.level().getBrightness(LightLayer.BLOCK, player.blockPosition()) < 8;
                    if (isDark || !player.level().isDay()) {
                        if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
                            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false, false));
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void witchery$deathHoodConfusion(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (!player.level().isClientSide && player.level() instanceof ServerLevel serverLevel) {
            if (DeathTransformationHelper.INSTANCE.hasDeathHood(player)) {
                if (player.tickCount % 20 == 0) {
                    List<Player> nearbyPlayers = player.level().getEntitiesOfClass(
                            Player.class,
                            player.getBoundingBox().inflate(8.0),
                            p -> p != player && p.hasLineOfSight(player)
                    );

                    for (Player target : nearbyPlayers) {
                        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false, true));
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, false, true));
                    }
                }
            }
        }
    }
}
