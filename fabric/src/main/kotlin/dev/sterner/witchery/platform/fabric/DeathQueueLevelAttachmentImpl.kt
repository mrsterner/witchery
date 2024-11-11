package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.DeathQueueLevelAttachment
import net.minecraft.server.level.ServerLevel
import java.util.UUID

object DeathQueueLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): DeathQueueLevelAttachment.Data {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.DEATH_QUEUE_LEVEL_DATA_TYPE)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data:  DeathQueueLevelAttachment.Data ) {
        level.setAttached(WitcheryFabricAttachmentRegistry.DEATH_QUEUE_LEVEL_DATA_TYPE, data)
    }
}