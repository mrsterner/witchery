package dev.sterner.witchery.mixin;

import dev.sterner.witchery.data_attachment.InventoryLockPlayerAttachment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {


    @Shadow @Final public NonNullList<Slot> slots;

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void witchery$preventClickOnLockedSlot(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        var self = (AbstractContainerMenu)(Object)this;
        if (slotId < 0 || slotId >= self.slots.size()) return;

        Slot slot = (self).slots.get(slotId);
        if (slot.container instanceof Inventory && slot.getContainerSlot() >= 0) {
            if (InventoryLockPlayerAttachment.INSTANCE.isSlotLocked(player, slot.getContainerSlot())) {
                ci.cancel();
            }
        }
    }
}