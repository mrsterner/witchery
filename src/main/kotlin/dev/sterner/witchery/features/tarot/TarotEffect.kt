package dev.sterner.witchery.features.tarot

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

abstract class TarotEffect(val cardNumber: Int) {

    abstract fun getDisplayName(isReversed: Boolean): Component
    abstract fun getDescription(isReversed: Boolean): Component

    open fun onAdded(player: Player, isReversed: Boolean) {}

    open fun onRemoved(player: Player, isReversed: Boolean) {}

    open fun onTick(player: Player, isReversed: Boolean) {}

    open fun onMorning(player: Player, isReversed: Boolean) {}

    open fun onNightfall(player: Player, isReversed: Boolean) {}

    open fun onBlockBreak(player: Player, blockState: BlockState, pos: BlockPos, isReversed: Boolean) {}

    open fun onEntityHit(player: Player, target: Entity, isReversed: Boolean) {}

    open fun onPlayerHurt(player: Player, source: DamageSource, amount: Float, isReversed: Boolean): Float {
        return amount
    }

    open fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {}

    open fun onItemUse(player: Player, item: ItemStack, isReversed: Boolean) {}

    open fun onEnterWater(player: Player, isReversed: Boolean) {}

    open fun onSleep(player: Player, isReversed: Boolean) {}

    protected fun removeCardFromReading(player: Player, cardNumber: Int) {
        val data = TarotPlayerAttachment.getData(player)
        val newCards = data.drawnCards.toMutableList()
        val newReversed = data.reversedCards.toMutableList()

        val index = newCards.indexOf(cardNumber)
        if (index != -1) {
            val wasReversed = newReversed.getOrNull(index) ?: false

            newCards.removeAt(index)
            if (index < newReversed.size) {
                newReversed.removeAt(index)
            }

            val newData = TarotPlayerAttachment.Data(
                drawnCards = newCards,
                reversedCards = newReversed,
                readingTimestamp = data.readingTimestamp
            )

            TarotPlayerAttachment.setData(player, newData)

            // Trigger onRemoved callback
            this.onRemoved(player, wasReversed)
        }
    }
}