package dev.sterner.witchery.handler.ability

interface PlayerAbilityType {
    val unlockLevel: Int
    val id: String

    fun isAvailable(level: Int): Boolean = level >= unlockLevel
}