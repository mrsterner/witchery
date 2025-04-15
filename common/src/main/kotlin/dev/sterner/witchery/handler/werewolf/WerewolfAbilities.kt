package dev.sterner.witchery.handler.werewolf

import dev.architectury.event.EventResult
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.payload.WerewolfAbilitySelectionC2SPayload
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

object WerewolfAbilities {
    private var abilityIndex = -1 // -1 means player is in the hotbar, not abilities

    /**
     * Handles the selection of abilities with the scroll wheel
     */
    fun scroll(minecraft: Minecraft?, x: Double, y: Double): EventResult? {
        val player = minecraft?.player
        if (minecraft == null || player == null) {
            return EventResult.pass()
        }

        val abilityCount = getAbilities(player).size
        if (abilityCount == 0) {
            return EventResult.pass()
        }

        if (abilityIndex == -1) {
            if (player.inventory.selected == 0 && y > 0.0) {
                abilityIndex = 0
                setAbilityIndex(player, abilityIndex)
                return EventResult.interruptFalse()
            } else if (player.inventory.selected == 8 && y < 0.0) {
                abilityIndex = abilityCount - 1
                setAbilityIndex(player, abilityIndex)
                return EventResult.interruptFalse()
            }
        } else {
            if (y > 0.0) {
                if (abilityIndex < abilityCount - 1) {
                    abilityIndex++
                    setAbilityIndex(player, abilityIndex)
                } else {
                    player.inventory.selected = 8
                    abilityIndex = -1
                    setAbilityIndex(player, abilityIndex)
                }
                return EventResult.interruptFalse()
            } else if (y < 0.0) {
                if (abilityIndex > 0) {
                    abilityIndex--
                    setAbilityIndex(player, abilityIndex)
                } else {
                    player.inventory.selected = 0
                    abilityIndex = -1
                    setAbilityIndex(player, abilityIndex)
                }
                return EventResult.interruptFalse()
            }
        }

        return EventResult.pass()
    }

    /**
     * This is used to select which ability is the selected one
     */
    @JvmStatic
    fun setAbilityIndex(player: Player, abilityIndex: Int) {
        updateAbilityIndex(player, abilityIndex)
        if (player.level().isClientSide()) {
            NetworkManager.sendToServer(WerewolfAbilitySelectionC2SPayload(abilityIndex))
        }
    }

    @JvmStatic
    fun updateAbilityIndex(player: Player, abilityIndex: Int) {
        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(abilityIndex = abilityIndex))
    }

    /**
     * Get all the available abilities for the current vampire level
     */
    @JvmStatic
    fun getAbilities(player: Player): List<WerewolfAbility> {
        val level = WerewolfPlayerAttachment.getData(player).getWerewolfLevel()
        return WerewolfAbility.entries.filter { it.unlockLevel <= level }
    }
}