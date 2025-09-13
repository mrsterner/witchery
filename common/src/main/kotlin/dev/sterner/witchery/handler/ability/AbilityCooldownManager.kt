package dev.sterner.witchery.handler.ability

import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.world.entity.player.Player

object AbilityCooldownManager {

    fun startVampireCooldown(player: Player, ability: VampireAbility) {
        val data = VampirePlayerAttachment.getData(player)
        val newCooldowns = data.abilityCooldowns.toMutableMap()
        newCooldowns[ability.id] = ability.cooldown
        VampirePlayerAttachment.setData(player, data.copy(abilityCooldowns = newCooldowns))
    }

    fun startWerewolfCooldown(player: Player, ability: WerewolfAbility) {
        val data = WerewolfPlayerAttachment.getData(player)
        val newCooldowns = data.abilityCooldowns.toMutableMap()
        newCooldowns[ability.id] = ability.cooldown
        WerewolfPlayerAttachment.setData(player, data.copy(abilityCooldowns = newCooldowns))
    }

    fun getVampireCooldown(player: Player, ability: VampireAbility): Int {
        return VampirePlayerAttachment.getData(player).abilityCooldowns[ability.id] ?: 0
    }

    fun getWerewolfCooldown(player: Player, ability: WerewolfAbility): Int {
        return WerewolfPlayerAttachment.getData(player).abilityCooldowns[ability.id] ?: 0
    }

    fun isVampireAbilityOnCooldown(player: Player, ability: VampireAbility): Boolean {
        return getVampireCooldown(player, ability) > 0
    }

    fun isWerewolfAbilityOnCooldown(player: Player, ability: WerewolfAbility): Boolean {
        return getWerewolfCooldown(player, ability) > 0
    }

    fun tickVampireCooldowns(player: Player) {
        val data = VampirePlayerAttachment.getData(player)
        val newCooldowns = data.abilityCooldowns.toMutableMap()
        var changed = false

        val iterator = newCooldowns.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value > 0) {
                entry.setValue(entry.value - 1)
                changed = true
                if (entry.value <= 0) {
                    iterator.remove()
                }
            }
        }

        if (changed) {
            VampirePlayerAttachment.setData(player, data.copy(abilityCooldowns = newCooldowns))
        }
    }

    fun tickWerewolfCooldowns(player: Player) {
        val data = WerewolfPlayerAttachment.getData(player)
        val newCooldowns = data.abilityCooldowns.toMutableMap()
        var changed = false

        val iterator = newCooldowns.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value > 0) {
                entry.setValue(entry.value - 1)
                changed = true
                if (entry.value <= 0) {
                    iterator.remove()
                }
            }
        }

        if (changed) {
            WerewolfPlayerAttachment.setData(player, data.copy(abilityCooldowns = newCooldowns))
        }
    }

    fun clearAllCooldowns(player: Player) {
        val vampData = VampirePlayerAttachment.getData(player)
        VampirePlayerAttachment.setData(player, vampData.copy(abilityCooldowns = mutableMapOf()))

        val wereData = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, wereData.copy(abilityCooldowns = mutableMapOf()))
    }
}