package dev.sterner.witchery.mixin;

import dev.sterner.witchery.Witchery;
import dev.sterner.witchery.core.data_attachment.InventoryLockPlayerAttachment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(InventoryScreen.class)
@OnlyIn(Dist.CLIENT)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {


    public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void witchery$renderLockedSlots(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.minecraft == null || this.minecraft.player == null) return;

        InventoryLockPlayerAttachment.Data data = InventoryLockPlayerAttachment.getData(this.minecraft.player);

        for (int lockedSlot : data.getLockedSlots()) {
            Slot slot = witchery_neoforge$findSlotForInventoryIndex(lockedSlot);
            if (slot != null) {
                witchery_neoforge$renderBarrierOverSlot(guiGraphics, slot);
            }
        }
    }

    @Unique
    private Slot witchery_neoforge$findSlotForInventoryIndex(int inventoryIndex) {
        for (Slot slot : this.menu.slots) {
            if (slot.container instanceof Inventory && slot.getContainerSlot() == inventoryIndex) {
                return slot;
            }
        }
        return null;
    }

    @Unique
    private void witchery_neoforge$renderBarrierOverSlot(GuiGraphics guiGraphics, Slot slot) {
        int x = this.leftPos + slot.x;
        int y = this.topPos + slot.y;

        guiGraphics.blit(
                Witchery.Companion.id("textures/gui/locked_slot.png"),
                x, y,
                0, 0,
                16, 16,
                16, 16
        );
    }
}