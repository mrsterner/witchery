package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.content.entity.BroomEntity
import net.minecraft.world.entity.player.Player

object PlayerMixinLogic {

    fun wantsStopRiding(original: Boolean, player: Player): Boolean {
        val vehicle = player.vehicle
        if (vehicle is BroomEntity) {
            return false
        }
        return original
    }
}