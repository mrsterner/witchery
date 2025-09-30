package dev.sterner.witchery.mixin.possession.client;


import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "isVisuallyCrawling", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInWater()Z"), cancellable = true)
    private void witchery$isVisuallyCrawling(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player && AfflictionPlayerAttachment.getData(player).isSoulForm()) {
            cir.setReturnValue(false);
        }
    }
}
