package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @WrapWithCondition(method = "renderItemHotbar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private boolean onRenderHotbar(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height) {
        int index = AfflictionAbilityHandler.INSTANCE.getAbilityIndex();

        return index == -1;
    }
}
