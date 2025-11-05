package dev.sterner.witchery.mixin.modonomicon.client;

import com.klikli_dev.modonomicon.book.BookIcon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookIcon.class)
public class BookIconMixin {

    @Final
    @Shadow
    private ResourceLocation texture;

    @Final
    @Shadow
    private int width;

    @Final
    @Shadow
    private int height;

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void witchery$fixTextureAspectRatio(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (this.texture != null) {
            float aspectRatio = (float) this.width / (float) this.height;
            int renderWidth, renderHeight;

            if (aspectRatio > 1.0f) {
                renderWidth = 16;
                renderHeight = (int) (16 / aspectRatio);
            } else {
                renderHeight = 16;
                renderWidth = (int) (16 * aspectRatio);
            }

            int offsetX = (16 - renderWidth) / 2;
            int offsetY = (16 - renderHeight) / 2;

            guiGraphics.blit(
                    this.texture,
                    x + offsetX,
                    y + offsetY,
                    renderWidth,
                    renderHeight,
                    0,
                    0,
                    this.width,
                    this.height,
                    this.width,
                    this.height
            );

            ci.cancel();
        }
    }
}