package dev.sterner.witchery.mixin;

import dev.sterner.witchery.data_attachment.InventoryLockPlayerAttachment;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {

    @Shadow
    public Container container;
    @Shadow private int slot;

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void witchery$preventPlaceInLockedSlot(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.container instanceof Inventory) {
            Inventory inventory = (Inventory) this.container;
            if (InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(inventory.player, this.slot)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void witchery$preventPickupFromLockedSlot(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (this.container instanceof Inventory && InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, this.slot)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private void witchery$preventSetInLockedSlot(ItemStack stack, CallbackInfo ci) {
        if (this.container instanceof Inventory) {
            Inventory inventory = (Inventory) this.container;
            if (InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(inventory.player, this.slot) && !stack.isEmpty()) {
                ci.cancel();
            }
        }
    }
}