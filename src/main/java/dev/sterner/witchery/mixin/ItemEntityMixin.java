package dev.sterner.witchery.mixin;

import dev.sterner.witchery.features.misc.InventoryLockPlayerAttachment;
import dev.sterner.witchery.core.registry.WitcheryItems;
import dev.sterner.witchery.core.registry.WitcheryPoppetRegistry;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
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

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void witchery$preventPickupToLockedSlot(Player player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object)this;
        ItemStack itemStack = self.getItem();

        int targetSlot = player.getInventory().getSlotWithRemainingSpace(itemStack);
        if (targetSlot == -1) {
            targetSlot = player.getInventory().getFreeSlot();
        }

        if (targetSlot != -1 && InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, targetSlot)) {
            ci.cancel();
        }
    }

    @Inject(method = "setUnderwaterMovement", at = @At("HEAD"), cancellable = true)
    private void witchery$preventPoppetFloat(CallbackInfo ci) {
        var self = (ItemEntity) (Object) this;
        var stack = self.getItem();

        if (stack.is(WitcheryItems.INSTANCE.getVOODOO_POPPET().get())) {
            var vec = self.getDeltaMovement();

            self.setDeltaMovement(new Vec3(
                    vec.x * 0.95,
                    vec.y - 0.01,
                    vec.z * 0.95
            ));

            ci.cancel();
        }
    }
}
