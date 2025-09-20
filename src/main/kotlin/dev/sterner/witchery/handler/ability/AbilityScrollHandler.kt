package dev.sterner.witchery.handler.ability

import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.client.event.InputEvent

class AbilityScrollHandler {

    fun <T : Enum<T>> handleScroll(
        event: InputEvent.MouseScrollingEvent,
        player: Player,
        y: Double,
        abilityHandler: AbilityHandler
    ) {
        val abilities = abilityHandler.getAbilities(player)
        val abilityCount = abilities.size

        if (abilityCount == 0) {
            if (abilityHandler.abilityIndex != -1) {
                abilityHandler.setAbilityIndex(player, -1)
            }
            return
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
                event.isCanceled = true
                return
            } else if (player.inventory.selected == 8 && y < 0.0) {
                index = abilityCount - 1
                abilityHandler.setAbilityIndex(player, index)
                event.isCanceled = true
                return
            }
            return
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
            event.isCanceled = true
            return
        }
    }
}