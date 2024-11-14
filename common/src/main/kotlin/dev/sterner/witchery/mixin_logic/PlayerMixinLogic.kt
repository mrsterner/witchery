package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.entity.BroomEntity
import net.minecraft.world.entity.player.Player

object PlayerMixinLogic {

    fun wantsStopRiding(original: Boolean): Boolean{
        val player = Player::class.java.cast(this)
        val vehicle = player.vehicle
        if (vehicle is BroomEntity) {
            return false
        }
        return original
    }
}