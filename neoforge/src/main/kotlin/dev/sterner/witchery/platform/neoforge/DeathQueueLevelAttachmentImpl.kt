package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.DEATH_QUEUE_LEVEL_DATA_ATTACHMENT
import dev.sterner.witchery.platform.DeathQueueLevelAttachment
import net.minecraft.server.level.ServerLevel

object DeathQueueLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): DeathQueueLevelAttachment.Data {
        return level.getData(DEATH_QUEUE_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: DeathQueueLevelAttachment.Data) {
        level.setData(DEATH_QUEUE_LEVEL_DATA_ATTACHMENT, data)
    }
}