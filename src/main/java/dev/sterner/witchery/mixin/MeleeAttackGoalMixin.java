package dev.sterner.witchery.mixin;

import dev.sterner.witchery.data_attachment.EtherealEntityAttachment;
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.affliction.AfflictionTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin   {


    @Shadow @Final protected PathfinderMob mob;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void witchery$preventAttackingOwner(CallbackInfoReturnable<Boolean> cir) {
        if (mob instanceof Zombie && mob.getTarget() instanceof Player player) {
            var etherealData = EtherealEntityAttachment.getData(mob);
            if (etherealData.getOwnerUUID() != null && etherealData.getOwnerUUID().equals(player.getUUID())) {
                cir.setReturnValue(false);
            }

            if (AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM) > 0) {
                if (etherealData.getOwnerUUID() != null) {
                    var owner = mob.level().getPlayerByUUID(etherealData.getOwnerUUID());
                    if (owner != null && AfflictionPlayerAttachment.getData(owner).getLevel(AfflictionTypes.LICHDOM) > 0) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}