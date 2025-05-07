package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.platform.EtherealEntityAttachment;
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {

    public NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @WrapWithCondition(method = "findTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;"))
    private boolean witchery$dontAttackSummonerOwner2(Level instance, TargetingConditions targetingConditions, LivingEntity livingEntity, double x, double y, double z){

        var player = instance.getNearestPlayer(targetingConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        if (player != null) {
            if (mob.getType().is(WitcheryTags.INSTANCE.getNECROMANCER_SUMMONABLE())) {
                var uuid = EtherealEntityAttachment.getData(mob).getOwnerUUID();
                if (uuid != null) {
                    if (player.getUUID().equals(uuid)) {
                        return false;
                    }
                }
            }
            if (mob.getType().is(EntityTypeTags.UNDEAD)) {
                boolean bl = VampirePlayerAttachment.getData(player).getVampireLevel() > 0;
                return !bl;
            }
        }

        return true;
    }
}
