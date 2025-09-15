package dev.sterner.witchery.handler.ability

import dev.architectury.event.EventResult
import net.minecraft.world.entity.player.Player

class AbilityScrollHandler {

    fun <T : Enum<T>> handleScroll(
        player: Player,
        y: Double,
        abilityHandler: AbilityHandler
    ): EventResult {
        val abilities = abilityHandler.getAbilities(player)
        val abilityCount = abilities.size

        if (abilityCount == 0) {
            if (abilityHandler.abilityIndex != -1) {
                abilityHandler.setAbilityIndex(player, -1)
            }
            return EventResult.pass()
        }

        var index = abilityHandler.abilityIndex

        if (index >= abilityCount) {
            index = -1
            abilityHandler.setAbilityIndex(player, -1)
        }

        if (index == -1) {
            // Entering ability bar from inventory
            if (player.inventory.selected == 0 && y > 0.0) {
                index = 0
                abilityHandler.setAbilityIndex(player, index)
                return EventResult.interruptTrue()
            } else if (player.inventory.selected == 8 && y < 0.0) {
                index = abilityCount - 1
                abilityHandler.setAbilityIndex(player, index)
                return EventResult.interruptTrue()
            }
            return EventResult.pass()
        } else {
            // In ability bar
            if (y > 0.0) {
                // Scrolling right
                if (index < abilityCount - 1) {
                    index++
                } else {
                    player.inventory.selected = 0
                    index = -1
                }
            } else if (y < 0.0) {
                // Scrolling left
                if (index > 0) {
                    index--
                } else {
                    player.inventory.selected = 8
                    index = -1
                }
            }

            abilityHandler.setAbilityIndex(player, index)
            return EventResult.interruptTrue()
        }
    }
}