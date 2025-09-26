package dev.sterner.witchery.handler.affliction.ability

import dev.sterner.witchery.handler.affliction.AfflictionAbility
import net.minecraft.world.entity.player.Player

interface AbilityHandler {
    val abilityIndex: Int
    fun getAbilities(player: Player): List<AfflictionAbility>
    fun setAbilityIndex(player: Player, index: Int)
    fun updateAbilityIndex(player: Player, index: Int)
}