package dev.sterner.witchery.api.interfaces

import net.minecraft.world.entity.player.Player

interface ProtoPossessable {
    val possessor: Player?
    val isBeingPossessed: Boolean
}