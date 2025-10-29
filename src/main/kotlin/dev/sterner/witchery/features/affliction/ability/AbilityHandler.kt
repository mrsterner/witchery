package dev.sterner.witchery.features.affliction.ability

import net.minecraft.world.entity.player.Player

interface AbilityHandler {
    val abilityIndex: Int?
    fun getAbilities(player: Player): List<AfflictionAbility>
    fun setAbilityIndex(player: Player, index: Int)
    fun updateAbilityIndex(player: Player, index: Int)
}