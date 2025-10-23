package dev.sterner.witchery.mixin;

import dev.sterner.witchery.features.tarot.TarotPlayerAttachment;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PanicGoal.class)
public class PanicGoalMixin {

    @Shadow @Final protected PathfinderMob mob;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void witchery$preventPanicNearLoversCard(CallbackInfoReturnable<Boolean> cir) {
        if (witchery$hasNearbyLoversCard()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void witchery$stopPanicNearLoversCard(CallbackInfoReturnable<Boolean> cir) {
        if (witchery$hasNearbyLoversCard()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean witchery$hasNearbyLoversCard() {
        AABB searchBox = this.mob.getBoundingBox().inflate(8.0);
        List<Player> nearbyPlayers = this.mob.level().getEntitiesOfClass(Player.class, searchBox);

        for (Player player : nearbyPlayers) {
            TarotPlayerAttachment.Data data = TarotPlayerAttachment.getData(player);

            int loversIndex = data.getDrawnCards().indexOf(7);
            if (loversIndex != -1) {
                boolean isReversed = data.getReversedCards().size() > loversIndex
                        && data.getReversedCards().get(loversIndex);

                if (!isReversed) {
                    return true;
                }
            }
        }

        return false;
    }
}