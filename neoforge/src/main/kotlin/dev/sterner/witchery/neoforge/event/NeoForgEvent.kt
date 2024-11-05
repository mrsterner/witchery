package dev.sterner.witchery.neoforge.event

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.BroomEntity
import dev.sterner.witchery.registry.WitcheryKeyMappings
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityMountEvent

@EventBusSubscriber(modid = Witchery.MODID)
object NeoForgEvent {

    @SubscribeEvent
    fun dismountBroom(event: EntityMountEvent) {
        /*
        if (!event.isMounting
            && event.entityBeingMounted.isAlive
            && event.entityMounting is Player
            && event.entityBeingMounted is BroomEntity
            && !WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING.isDown){

            val player = event.entityMounting as Player
            val broom = event.entityBeingMounted as BroomEntity
            broom.inputShift = player.isShiftKeyDown

            event.isCanceled = true
        }

         */
    }
}