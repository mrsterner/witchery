package dev.sterner.witchery.handler.affliction

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

interface AfflictionAbility {
    val id: String
    val affliction: AfflictionTypes
    val requiredLevel: Int
    val cooldown: Int
    val passive: Boolean
        get() = false
    val requiresTarget: Boolean
        get() = false

    fun use(player: Player): Boolean = false

    fun use(player: Player, target: Entity): Boolean = false

    fun isAvailable(player: Player, level: Int): Boolean {
        return level >= requiredLevel
    }

    fun tick(player: Player) {}
}
