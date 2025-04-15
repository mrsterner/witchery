package dev.sterner.witchery.handler.ability

import dev.architectury.event.EventResult
import net.minecraft.world.entity.player.Player

class AbilityScrollHandler {

    fun <T : Enum<T>> handleScroll(
        player: Player,
        y: Double,
        abilityHandler: AbilityHandler<T>
    ): EventResult {
        val abilityCount = abilityHandler.getAbilities(player).size
        if (abilityCount == 0) return EventResult.pass()

        var index = abilityHandler.abilityIndex

        if (index == -1) {
            if (player.inventory.selected == 0 && y > 0.0) {
                index = 0
            } else if (player.inventory.selected == 8 && y < 0.0) {
                index = abilityCount - 1
            } else return EventResult.pass()
        } else {
            if (y > 0.0) {
                if (index < abilityCount - 1) index++ else {
                    player.inventory.selected = 8
                    index = -1
                }
            } else if (y < 0.0) {
                if (index > 0) index-- else {
                    player.inventory.selected = 0
                    index = -1
                }
            }
        }

        abilityHandler.setAbilityIndex(player, index)
        return EventResult.interruptFalse()
    }
}