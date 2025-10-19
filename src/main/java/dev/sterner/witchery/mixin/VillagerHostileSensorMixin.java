package dev.sterner.witchery.mixin;

import com.google.common.collect.ImmutableMap;
import dev.sterner.witchery.core.registry.WitcheryMobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostileSensorMixin {

    @Shadow
    @Final
    private static ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES;

    @Inject(method = "isHostile", at = @At("RETURN"), cancellable = true)
    private void witchery$isHostile(LivingEntity entity, CallbackInfoReturnable<Boolean> cir){
        if (entity instanceof Player && entity.hasEffect(WitcheryMobEffects.INSTANCE.getGROTESQUE())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isClose", at = @At("HEAD"), cancellable = true)
    private void witchery$isClose(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir){
        if (target instanceof Player && target.hasEffect(WitcheryMobEffects.INSTANCE.getGROTESQUE())) {
            float distance = 12.0f;
            boolean isClose = target.distanceToSqr(attacker) <= (double)(distance * distance);
            cir.setReturnValue(isClose);
        } else if (!ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(target.getType())) {
            cir.setReturnValue(false);
        }
    }
}
