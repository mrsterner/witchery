package dev.sterner.witchery.mixin.possession.possessed;

import dev.sterner.witchery.api.interfaces.Possessable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(HurtByTargetGoal.class)
public abstract class HurtByTargetGoalMixin extends TargetGoal {
    public HurtByTargetGoalMixin(Mob mob, boolean checkSight) {
        super(mob, checkSight);
    }

    @ModifyArg(method = "alertOthers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private Predicate<Entity> stopCallingPossessedMobs(Predicate<Entity> basePredicate) {
        return basePredicate.and(e -> this.mob.getLastHurtByMob() != e || !((Possessable)e).isBeingPossessed() || this.mob instanceof ZombifiedPiglin);
    }
}