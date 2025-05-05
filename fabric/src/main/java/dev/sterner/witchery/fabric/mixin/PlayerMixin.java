package dev.sterner.witchery.fabric.mixin;

import dev.sterner.witchery.platform.WitcheryAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "createAttributes", require = 1, allow = 1, at = @At("RETURN"))
    private static void witchery$additionalEntityAttributes$addPlayerAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        info.getReturnValue().add(WitcheryAttributes.INSTANCE.getVAMPIRE_BAT_FORM_DURATION());
        info.getReturnValue().add(WitcheryAttributes.INSTANCE.getVAMPIRE_DRINK_SPEED());
        info.getReturnValue().add(WitcheryAttributes.INSTANCE.getVAMPIRE_SUN_RESISTANCE());
    }

}