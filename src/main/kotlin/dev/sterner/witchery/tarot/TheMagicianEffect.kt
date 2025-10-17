package dev.sterner.witchery.tarot

import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.item.brew.BrewItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class TheMagicianEffect : TarotEffect(2) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Magician (Reversed)" else "The Magician"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Your magic turns against you" else "Mastery over the arcane"
    )

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (player.level() !is ServerLevel) return

        val altar = AltarBlockEntity.getClosestAltar(
            player.level() as ServerLevel,
            player.blockPosition(),
            16
        )

        if (isReversed) {
            altar?.let {
                it.currentPower = (it.currentPower - 500).coerceAtLeast(0)
            }
        } else {
            altar?.let {
                it.currentPower = (it.currentPower + 1000).coerceAtMost(it.maxPower)
            }
        }
    }

    override fun onItemUse(player: Player, item: ItemStack, isReversed: Boolean) {
        if (!isReversed && item.item is BrewItem) {
            if (player.level().random.nextFloat() < 0.2f) {
                //TODO dont consume brew
                player.displayClientMessage(
                    Component.literal("Not implemented yet").withStyle(ChatFormatting.LIGHT_PURPLE),
                    true
                )
            }
        }
    }
}