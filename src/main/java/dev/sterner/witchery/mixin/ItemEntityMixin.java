package dev.sterner.witchery.mixin;

import dev.sterner.witchery.registry.WitcheryItems;
import dev.sterner.witchery.registry.WitcheryPoppetRegistry;
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
            var voodooPoppetType = WitcheryPoppetRegistry.getType(WitcheryItems.INSTANCE.getVOODOO_POPPET().get());
            if (voodooPoppetType != null) {
                voodooPoppetType.handleItemEntityTick(entity);
            }
        }
    }
}
