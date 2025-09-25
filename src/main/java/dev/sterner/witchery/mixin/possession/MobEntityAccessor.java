package dev.sterner.witchery.mixin.possession;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobEntityAccessor {
    @Accessor
    GoalSelector getTargetSelector();

    @Accessor
    GoalSelector getGoalSelector();

    @Accessor("navigation")
    PathNavigation requiem$getNavigation();
}