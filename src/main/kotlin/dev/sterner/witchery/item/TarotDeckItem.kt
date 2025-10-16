package dev.sterner.witchery.item

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.TarotPlayerAttachment
import dev.sterner.witchery.payload.OpenTarotScreenS2CPayload
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor

class TarotDeckItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)

        if (!level.isClientSide && player is ServerPlayer) {
            val data = TarotPlayerAttachment.getData(player)
            if (data.drawnCards.isEmpty()) {
                PacketDistributor.sendToPlayer(player, OpenTarotScreenS2CPayload())
            } else {
                player.displayClientMessage(
                    Component.literal("Your fortune this week has already been decided.")
                        .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC),
                    true
                )

            }
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            Component.translatable("item.witchery.tarot_deck.desc")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        )

        tooltipComponents.add(
            Component.literal("Draw 3 cards from the Major Arcana")
                .withStyle(ChatFormatting.DARK_PURPLE)
        )

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}