package dev.sterner.witchery.mixin;

import dev.sterner.witchery.registry.WitcheryMobEffects;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(AvoidEntityGoal.class)
public abstract class AvoidEntityGoalMixin<T extends LivingEntity> {

    @Shadow @Final protected Class<T> avoidClass;
    @Shadow @Final protected PathfinderMob mob;
    @Shadow @Final protected float maxDist;
    @Shadow @Final protected Predicate<LivingEntity> predicateOnAvoidEntity;
    @Shadow @Final protected Predicate<LivingEntity> avoidPredicate;
    @Shadow protected T toAvoid;
    @Shadow protected Path path;

    /**
     * Inject at the head of canUse to check for players with the grotesque effect
     * before the standard entity avoidance logic runs
     */
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void witchery$onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (avoidClass != Player.class && mob.getType().is(WitcheryTags.INSTANCE.getSCARED_BY_GROTESQUE())) {
            Player grotesquePlayer = witchery$findNearestGrotesquePlayer();
            
            if (grotesquePlayer != null) {
                this.toAvoid = (T) grotesquePlayer;

                Vec3 escapePos = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
                
                if (escapePos == null) {
                    cir.setReturnValue(false);
                    return;
                }

                if (this.toAvoid.distanceToSqr(escapePos.x, escapePos.y, escapePos.z) < 
                        this.toAvoid.distanceToSqr(this.mob)) {
                    cir.setReturnValue(false);
                    return;
                }

                this.path = this.mob.getNavigation().createPath(escapePos.x, escapePos.y, escapePos.z, 0);
                
                if (this.path != null) {
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    /**
     * Find the nearest player with the grotesque effect
     */
    @Unique
    private Player witchery$findNearestGrotesquePlayer() {
        Predicate<Player> hasGrotesqueEffect = player ->
                player.hasEffect(WitcheryMobEffects.INSTANCE.getGROTESQUE());
        
        Predicate<LivingEntity> combinedPredicate = livingEntity -> {
                if (!(livingEntity instanceof Player player)) {
                    return false;
                }
                return hasGrotesqueEffect.test(player) &&
                        predicateOnAvoidEntity.test(player) &&
                        avoidPredicate.test(player);
        };
        
        return mob.level().getNearestPlayer(
                TargetingConditions.forCombat().range(maxDist).selector(combinedPredicate),
                mob,
                mob.getX(),
                mob.getY(),
                mob.getZ()
        );
    }
}