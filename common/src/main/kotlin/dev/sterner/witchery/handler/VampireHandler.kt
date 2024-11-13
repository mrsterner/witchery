package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

object VampireHandler {

    private var abilityIndex = -1 // -1 means player is in the hotbar, not abilities

    fun scroll(minecraft: Minecraft?, x: Double, y: Double): EventResult? {
        val player = minecraft?.player
        if (minecraft == null || player == null) {
            return EventResult.pass()
        }

        val abilityCount = VampirePlayerAttachment.getAbilities(player).size
        if (abilityCount == 0) {
            return EventResult.pass()
        }

        if (abilityIndex == -1) {
            if (player.inventory.selected == 0 && y > 0.0) {
                abilityIndex = 0
                activateAbility(player, abilityIndex)
                return EventResult.interruptTrue()
            } else if (player.inventory.selected == 8 && y < 0.0) {
                abilityIndex = abilityCount - 1
                activateAbility(player, abilityIndex)
                return EventResult.interruptTrue()
            }
        } else {
            if (y > 0.0) {
                if (abilityIndex < abilityCount - 1) {
                    abilityIndex++
                    activateAbility(player, abilityIndex)
                } else {
                    player.inventory.selected = 8
                    abilityIndex = -1
                    activateAbility(player, abilityIndex)
                }
                return EventResult.interruptTrue()
            } else if (y < 0.0) {
                if (abilityIndex > 0) {
                    abilityIndex--
                    activateAbility(player, abilityIndex)
                } else {
                    player.inventory.selected = 0
                    abilityIndex = -1
                    activateAbility(player, abilityIndex)
                }
                return EventResult.interruptTrue()
            }
        }

        return EventResult.pass()
    }

    private fun activateAbility(player: Player, index: Int) {
        VampirePlayerAttachment.setAbilityIndex(player, index)
        val ability = if (index < 0) {
            null
        } else {
            VampirePlayerAttachment.getAbilities(player)[index]
        }

        player.displayClientMessage(Component.literal("Selected ability: ${ability}"), true)
    }

    @JvmStatic
    fun interactEntity(player: Player?, entity: Entity?, interactionHand: InteractionHand?): EventResult? {

        if (player != null) {
            val data = VampirePlayerAttachment.getData(player)
            println(data.abilityIndex)
        }

        return EventResult.pass()
    }
}