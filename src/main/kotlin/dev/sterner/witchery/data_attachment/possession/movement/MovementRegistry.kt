package dev.sterner.witchery.data_attachment.possession.movement


import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

interface MovementRegistry {
    fun getEntityMovementConfig(type: EntityType<*>): MovementAltererAttachment.SerializableMovementConfig?

    companion object {
        fun get(world: Level?): MovementRegistry {
            return if (world?.isClientSide == true) {
                ClientMovementRegistry
            } else {
                MovementAltererManager
            }
        }
    }
}
