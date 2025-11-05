package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment;
import dev.sterner.witchery.core.registry.WitcheryTags;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<A extends LivingEntity> extends TargetGoal {

    public NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @WrapOperation(
            method = "findTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            )
    )
    private <T extends LivingEntity> List<T> witchery$filterPetrified(Level instance, Class<T> aClass, AABB aabb, Predicate<T> predicate, Operation<List<T>> original) {
        Predicate<T> combined = predicate.and(entity -> !PetrifiedEntityAttachment.INSTANCE.getData(entity).isPetrified());
        return instance.getEntitiesOfClass(aClass, aabb, combined);
    }

    @WrapOperation(
            method = "findTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;"
            )
    )
    private Player witchery$dontTarget(Level instance, TargetingConditions conditions, LivingEntity mob, double x, double y, double z, Operation<Player> original) {
        Player player = original.call(instance, conditions, mob, x, y, z);

        if (player == null) return null;

        if (PetrifiedEntityAttachment.INSTANCE.getData(player).isPetrified()) {
            return null;
        }

        if (mob.getType().is(WitcheryTags.INSTANCE.getNECROMANCER_SUMMONABLE())) {
            var uuid = EtherealEntityAttachment.getData(mob).getOwnerUUID();
            if (uuid != null && uuid.equals(player.getUUID())) {
                return null;
            }
        }

        if (mob.getType().is(EntityTypeTags.UNDEAD)) {
            if (AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0) {
                return null;
            }
        }

        return player;
    }
}
