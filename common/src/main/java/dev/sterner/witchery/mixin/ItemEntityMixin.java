package dev.sterner.witchery.mixin;

import dev.sterner.witchery.handler.PoppetHandler;
import dev.sterner.witchery.registry.WitcheryItems;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void witchery$onTick(CallbackInfo ci) {
        var entity = (ItemEntity) (Object) this;
        if (entity.getItem().is(WitcheryItems.INSTANCE.getVOODOO_POPPET().get())) {
            PoppetHandler.INSTANCE.handleVoodoo(entity);
        }
    }
}
