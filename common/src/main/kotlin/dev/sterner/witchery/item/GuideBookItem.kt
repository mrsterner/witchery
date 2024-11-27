package dev.sterner.witchery.item

import com.klikli_dev.modonomicon.client.gui.BookGuiManager
import com.klikli_dev.modonomicon.client.gui.book.BookAddress
import com.klikli_dev.modonomicon.data.BookDataManager
import com.klikli_dev.modonomicon.item.ModonomiconItem
import dev.sterner.witchery.Witchery
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level


class GuideBookItem(pProperties: Properties) : ModonomiconItem(pProperties.stacksTo(1)) {

    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemInHand = pPlayer.getItemInHand(pUsedHand)
        if (pLevel.isClientSide) {
            val book = BookDataManager.get().getBook(ID)
            BookGuiManager.get().openBook(BookAddress.defaultFor(book))
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, pLevel.isClientSide)
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        //NoOp
    }

    companion object {
        val ID = Witchery.id("guidebook")
    }
}