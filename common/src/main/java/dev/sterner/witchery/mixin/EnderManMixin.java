package dev.sterner.witchery.mixin;

import dev.sterner.witchery.registry.WitcheryMobEffects;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EnderManMixin {

    @Inject(method = "teleport(DDD)Z", at = @At("HEAD"), cancellable = true)
    private void teleport(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        var enderMan = EnderMan.class.cast(this);
        if (enderMan.hasEffect(WitcheryMobEffects.INSTANCE.getENDER_BOUND())) {
            cir.setReturnValue(false);
        }
    }
}
