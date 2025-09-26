package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorMixin {

    @Dynamic("Lambda method injection")
    @Inject(method = "lambda$static$3", at = @At("RETURN"), cancellable = true)
    private static void exceptCreativeOrSpectator(@Nullable Entity tested, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValueZ() && tested instanceof Player player && AfflictionPlayerAttachment.getData(player).isVagrant()) {
            info.setReturnValue(false);
        }
    }
}