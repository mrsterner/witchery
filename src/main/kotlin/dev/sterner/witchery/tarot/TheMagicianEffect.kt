package dev.sterner.witchery.tarot

import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.item.brew.BrewItem
import dev.sterner.witchery.item.brew.ThrowableBrewItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import java.util.UUID

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
        if (!isReversed && (item.item is BrewItem || item.item is ThrowableBrewItem)) {
            if (player.level().random.nextFloat() < 0.2f) {
                if (player is ServerPlayer) {
                    TheMagicianBrewReturn.scheduleReturn(player, item.copy())
                }
            }
        }
    }

    object TheMagicianBrewReturn {
        private val scheduledReturns = mutableMapOf<UUID, ItemStack>()

        fun scheduleReturn(player: ServerPlayer, item: ItemStack) {
            scheduledReturns[player.uuid] = item
        }

        fun tick(player: Player) {
            if (player is ServerPlayer) {
                val item = scheduledReturns.remove(player.uuid) ?: return

                if (!player.inventory.add(item)) {
                    player.drop(item, false)
                }
            }
        }
    }
}