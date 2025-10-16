package dev.sterner.witchery.mixin;

import com.klikli_dev.modonomicon.book.BookCategory;
import com.klikli_dev.modonomicon.client.gui.book.node.BookCategoryNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import dev.sterner.witchery.Witchery;
import dev.sterner.witchery.datagen.book.WitcherySubBookProvider;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(BookCategoryNodeScreen.class)
public abstract class BookCategoryNodeScreenMixin {

    @Shadow
    @Final
    private BookParentNodeScreen bookParentScreen;

    @Shadow public abstract BookCategory getCategory();

    @ModifyVariable(
            method = "renderEntries",
            at = @At(value = "STORE", ordinal = 0), // ordinal = 0 points to the first store operation of "height"
            name = "height"
    )
    private int witchery$modifyUnreadIconHeight(int height) {
        if (this.bookParentScreen.getBook().getId().equals(Witchery.Companion.id("guidebook"))) {
            return height;
        }
        return height;
    }


    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;enableScissor(IIII)V"
            )
    )
    private void witchery$adjustScissor(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        if (this.bookParentScreen.getBook().getId().equals(Witchery.Companion.id("guidebook"))) {
            int adjustedX1 = x1 + 3;
            int adjustedY1 = y1 + 3;
            int adjustedX2 = x2 - 2;
            int adjustedY2 = y2 - 2;

            guiGraphics.enableScissor(adjustedX1, adjustedY1, adjustedX2, adjustedY2);
        } else {
            guiGraphics.enableScissor(x1, y1, x2, y2);
        }
    }
}