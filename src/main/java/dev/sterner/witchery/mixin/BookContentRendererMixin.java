package dev.sterner.witchery.mixin;

import com.klikli_dev.modonomicon.client.gui.book.BookContentRenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sterner.witchery.datagen.book.WitcherySubBookProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookContentRenderer.class, remap = false)
public class BookContentRendererMixin {

    @Inject(
            method = "renderBookBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
            ),
            cancellable = true
    )
    private static void witchery$renderBookBackground(
            GuiGraphics guiGraphics,
            ResourceLocation bookContentTexture,
            CallbackInfo ci
    ) {
        if (bookContentTexture == WitcherySubBookProvider.Companion.getBOOK_CONTENT()) {
            guiGraphics.blit(bookContentTexture, 0, 0, 0, 0, 272, 182, 512, 256);
            ci.cancel();
        }
    }
}