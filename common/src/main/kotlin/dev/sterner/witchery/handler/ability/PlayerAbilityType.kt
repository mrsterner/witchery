package dev.sterner.witchery.handler.ability

interface PlayerAbilityType {
    val unlockLevel: Int
    val id: String
    val cooldown: Int

    fun isAvailable(level: Int): Boolean = level >= unlockLevel
}