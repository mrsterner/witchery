package dev.sterner.witchery.mixin;

import dev.sterner.witchery.api.WitcheryApi;
import dev.sterner.witchery.handler.vampire.VampireLeveling;
import dev.sterner.witchery.platform.EtherealEntityAttachment;
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class)
public abstract class TargetGoalMixin {

    @Shadow @Final protected Mob mob;

    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void witchery$dontAttackSummonerOwner(LivingEntity potentialTarget, TargetingConditions targetPredicate, CallbackInfoReturnable<Boolean> cir){
        if (mob.getType().is(WitcheryTags.INSTANCE.getNECROMANCER_SUMMONABLE())) {
            var uuid = EtherealEntityAttachment.getData(mob).getOwnerUUID();
            if (uuid != null) {
                if (potentialTarget.getUUID().equals(uuid)) {
                    cir.setReturnValue(false);
                }
            }
        }
        if (mob.getType().is(EntityTypeTags.UNDEAD) && potentialTarget instanceof Player player) {
            boolean bl = VampirePlayerAttachment.getData(player).getVampireLevel() > 0;
            if (bl) {
                cir.setReturnValue(false);
            }
        }
    }
}
