package dev.sterner.witchery.handler.ability

import net.minecraft.world.entity.player.Player

interface AbilityHandler<T : Enum<T>> {
    val abilityIndex: Int
    fun getLevel(player: Player): Int
    fun getAbilities(player: Player): List<T>
    fun setAbilityIndex(player: Player, index: Int)
    fun updateAbilityIndex(player: Player, index: Int)
}