package dev.sterner.witchery.core.api.interfaces

import net.minecraft.world.entity.player.Player
import org.jetbrains.annotations.ApiStatus


interface Possessable : PossessableBase {
    override val possessor: Player?

    override val isBeingPossessed: Boolean
        get() = false

    fun canBePossessedBy(player: Player?): Boolean {
        return true
    }

    @ApiStatus.Internal
    fun setPossessor(possessor: Player?) {
    }

    @ApiStatus.OverrideOnly
    fun onPossessorSet(possessor: Player?) {
    }

    val isRegularEater: Boolean
        get() = false
}