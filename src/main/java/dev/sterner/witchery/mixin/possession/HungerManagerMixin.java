package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class HungerManagerMixin {

    @Shadow private int foodLevel;

    @Shadow private float exhaustionLevel;

    @Inject(method = "tick", at = @At(value = "HEAD", ordinal = 0))
    private void updateSoulHunger(Player player, CallbackInfo ci) {
        if (AfflictionPlayerAttachment.getData(player).isSoulForm()) {
            this.exhaustionLevel = 0;
            this.foodLevel = 20;
        }
    }
}
