package dev.sterner.witchery.api.interfaces

import net.minecraft.world.entity.player.Player
import javax.annotation.Nullable

interface IPossessable {
    val possessor: Player?
    val isBeingPossessed: Boolean
}