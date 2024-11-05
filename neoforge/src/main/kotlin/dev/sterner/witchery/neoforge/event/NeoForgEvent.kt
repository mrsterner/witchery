package dev.sterner.witchery.neoforge.event

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.BroomEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityMountEvent

@EventBusSubscriber(modid = Witchery.MODID)
object NeoForgEvent {

    @SubscribeEvent
    fun dismountBroom(event: EntityMountEvent) {

        if (!event.level.isClientSide() && !event.isMounting && event.entityBeingMounted.isAlive && event.entityMounting is Player && event.entityBeingMounted is BroomEntity){
            event.isCanceled = true
        }

    }
}