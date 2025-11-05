package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TargetingConditions.class)
public class TargetingConditionsMixin {

    @ModifyArg(method = "test", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"), index = 1)
    private double witchery$setMobDetectionMinimum(double original, @Local(ordinal = 0, argsOnly = true) LivingEntity attacker, @Local(ordinal = 1, argsOnly = true) LivingEntity targetEntity) {
        if (PetrifiedEntityAttachment.INSTANCE.getData(targetEntity).isPetrified()) {
            return 0;
        }

        if (attacker instanceof Zombie && targetEntity instanceof Player player) {
            if (AfflictionPlayerAttachment.getData(player).getVampireLevel() >= 10) {
                return 0;
            }
        }

        return original;
    }
}