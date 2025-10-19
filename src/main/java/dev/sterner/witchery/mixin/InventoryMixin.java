package dev.sterner.witchery.mixin;

import dev.sterner.witchery.core.data_attachment.InventoryLockPlayerAttachment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Shadow
    @Final
    public Player player;

    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void witchery$preventAddToLockedSlot(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;

        Inventory self = (Inventory)(Object)this;
        int targetSlot = self.getSlotWithRemainingSpace(stack);
        if (targetSlot == -1) {
            targetSlot = self.getFreeSlot();
        }

        if (targetSlot != -1 && InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, targetSlot)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void witchery$preventAddToSpecificLockedSlot(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (player != null && InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, slot)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void witchery$preventSetItemInLockedSlot(int slot, ItemStack stack, CallbackInfo ci) {
        if (player != null && InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, slot) && !stack.isEmpty()) {
            ci.cancel();
        }
    }

    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void witchery$preventRemoveFromLockedSlot(int slot, int count, CallbackInfoReturnable<ItemStack> cir) {
        if (player != null && InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, slot)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "getSelected", at = @At("HEAD"), cancellable = true)
    private void witchery$preventUseOfLockedHotbarSlot(CallbackInfoReturnable<ItemStack> cir) {
        var self = (Inventory)(Object)this;
        if (player != null && InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, self.selected)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}