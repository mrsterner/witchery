package dev.sterner.witchery.api.interfaces

import net.minecraft.world.entity.player.Player
import javax.annotation.Nullable

interface IPossessable {
    @Nullable
    fun getPossessor(): Player?

    fun isBeingPossessed(): Boolean
}