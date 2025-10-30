package dev.sterner.witchery.mixin.guardvillagers;

import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallestegg.guardvillagers.common.entities.Guard;

@Mixin(Mob.class)
public class MobMixin {

    @Shadow @Final public GoalSelector targetSelector;

    @Unique
    private static boolean witchery$isVampHelper(LivingEntity target) {
        if (!(target instanceof Player player)) return false;
        return AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0;
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void witchery$registerGoals(CallbackInfo ci) {
        Mob self = (Mob)(Object)this;
        if (self instanceof Guard guard) {
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>(
                    guard,
                    Player.class,
                    10,
                    true,
                    false,
                    MobMixin::witchery$isVampHelper
            ));
        }
    }
}
