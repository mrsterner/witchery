package dev.sterner.witchery.handler.ability

import dev.architectury.event.EventResult
import net.minecraft.world.entity.player.Player


// Modified AbilityScrollHandler to better handle hotbar edges
class AbilityScrollHandler {
    fun <T : Enum<T>> handleScroll(
        player: Player,
        y: Double,
        abilityHandler: AbilityHandler<T>
    ): EventResult {
        val abilities = abilityHandler.getAbilities(player)
        val abilityCount = abilities.size
        if (abilityCount == 0) return EventResult.pass()

        var index = abilityHandler.abilityIndex

        if (index == -1) {
            // Entering ability selection from hotbar edges
            if (player.inventory.selected == 0 && y > 0.0) {
                // We're at leftmost hotbar slot and scrolling right
                index = 0
                abilityHandler.setAbilityIndex(player, index)
                return EventResult.interruptTrue()
            } else if (player.inventory.selected == 8 && y < 0.0) {
                // We're at rightmost hotbar slot and scrolling left
                index = abilityCount - 1
                abilityHandler.setAbilityIndex(player, index)
                return EventResult.interruptTrue()
            } else {
                // Regular hotbar scrolling
                return EventResult.pass()
            }
        } else {
            // Already in ability selection mode
            if (y > 0.0) {
                // Scrolling right
                if (index < abilityCount - 1) {
                    // Move to next ability
                    index++
                } else {
                    // Exit ability selection and go to hotbar
                    player.inventory.selected = 8
                    index = -1
                }
            } else if (y < 0.0) {
                // Scrolling left
                if (index > 0) {
                    // Move to previous ability
                    index--
                } else {
                    // Exit ability selection and go to hotbar
                    player.inventory.selected = 0
                    index = -1
                }
            }

            abilityHandler.setAbilityIndex(player, index)
            return EventResult.interruptTrue()
        }
    }
}