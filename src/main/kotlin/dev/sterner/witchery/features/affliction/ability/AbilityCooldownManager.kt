package dev.sterner.witchery.features.affliction.ability

import dev.sterner.witchery.features.affliction.AfflictionAbility
import net.minecraft.world.entity.player.Player
import java.util.*

object AbilityCooldownManager {

    private val cooldowns = mutableMapOf<UUID, MutableMap<AfflictionAbility, Int>>()

    fun isOnCooldown(player: Player, ability: AfflictionAbility): Boolean {
        return cooldowns[player.uuid]?.get(ability)?.let { it > 0 } ?: false
    }

    fun startCooldown(player: Player, ability: AfflictionAbility) {
        cooldowns.computeIfAbsent(player.uuid) { mutableMapOf() }[ability] = ability.cooldown
    }

    fun getCooldown(player: Player, ability: AfflictionAbility): Int {
        return cooldowns[player.uuid]?.get(ability) ?: 0
    }

    fun tick(player: Player) {
        cooldowns[player.uuid]?.let { map ->
            val iterator = map.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val newTime = entry.value - 1
                if (newTime <= 0) iterator.remove()
                else map[entry.key] = newTime
            }
        }
    }
}
