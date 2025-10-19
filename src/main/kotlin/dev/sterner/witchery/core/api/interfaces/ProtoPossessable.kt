package dev.sterner.witchery.core.api.interfaces

import net.minecraft.world.entity.player.Player

interface ProtoPossessable {
    val possessor: Player?
        get() = null

    val isBeingPossessed: Boolean
        get() = false
}