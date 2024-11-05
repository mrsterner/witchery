package dev.sterner.witchery.mixin;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.data.BookDataManager;
import dev.sterner.witchery.item.GuideBookItem;
import dev.sterner.witchery.registry.WitcheryItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Thank you MyNamesRaph for letting us look at your code for this.
 * Code: https://github.com/MyNamesRaph/Mystcraft-Ageless/blob/master/src/main/java/com/mynamesraph/mystcraft/mixin/LecternBlockMixin.java
 * License: https://github.com/MyNamesRaph/Mystcraft-Ageless/blob/master/LICENSE
 */

@Mixin(LecternBlock.class)
public class LecternBlockMixin {
    @Shadow @Final public static BooleanProperty HAS_BOOK;

    @Unique
    private static final BooleanProperty WITCHERY = BooleanProperty.create("witchery");

    @Inject(method = "createBlockStateDefinition", at = @At("RETURN"))
    private void witchery$addNewProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(WITCHERY);
    }

    @Inject(at= @At("RETURN"), method = "placeBook")
    private static void witchery$placeBook(LivingEntity entity, Level level, BlockPos pos, BlockState state, ItemStack stack, CallbackInfo ci) {
        if(level.getBlockEntity(pos) instanceof LecternBlockEntity lectern)
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(WITCHERY, lectern.getBook().is(WitcheryItems.INSTANCE.getGUIDEBOOK().get())));
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void witchery$openLecternGuidebook(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.getValue(HAS_BOOK) && state.getValue(WITCHERY)) {
            if (level.isClientSide) {
                Book book = BookDataManager.get().getBook(GuideBookItem.Companion.getID());
                BookGuiManager.get().openBook(BookAddress.defaultFor(book));
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
