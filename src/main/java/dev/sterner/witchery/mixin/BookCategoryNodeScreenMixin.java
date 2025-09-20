package dev.sterner.witchery.mixin;

import com.klikli_dev.modonomicon.client.gui.book.node.BookCategoryNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import dev.sterner.witchery.Witchery;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(BookCategoryNodeScreen.class)
public abstract class BookCategoryNodeScreenMixin {

    @Shadow
    @Final
    private BookParentNodeScreen bookParentScreen;

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
}