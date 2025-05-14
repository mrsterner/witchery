package dev.sterner.witchery.mixin;

import dev.sterner.witchery.registry.WitcheryMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostileSensorMixin {

    @Inject(method = "isHostile", at = @At("RETURN"), cancellable = true)
    private void witchery$isHostile(LivingEntity entity, CallbackInfoReturnable<Boolean> cir){
        if (entity instanceof Player && entity.hasEffect(WitcheryMobEffects.INSTANCE.getGROTESQUE())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isClose", at = @At("RETURN"), cancellable = true)
    private void witchery$isClose(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir){
        if (attacker instanceof Player && attacker.hasEffect(WitcheryMobEffects.INSTANCE.getGROTESQUE())) {
            cir.setReturnValue(target.distanceToSqr(attacker) <= 12 * 12);
        }
    }
}
