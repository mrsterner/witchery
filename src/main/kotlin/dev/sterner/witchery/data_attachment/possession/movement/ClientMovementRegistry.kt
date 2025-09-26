package dev.sterner.witchery.data_attachment.possession.movement

import net.minecraft.world.entity.EntityType

object ClientMovementRegistry : MovementRegistry {
    val entityMovementConfigs = mutableMapOf<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>()

    override fun getEntityMovementConfig(type: EntityType<*>): MovementAltererAttachment.SerializableMovementConfig? {
        return entityMovementConfigs[type]
    }
}
