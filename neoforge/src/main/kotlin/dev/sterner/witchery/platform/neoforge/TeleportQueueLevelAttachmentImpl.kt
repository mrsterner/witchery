package dev.sterner.witchery.platform.neoforge


import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.TELEPORT_QUEUE_DATA_ATTACHMENT
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment.Data
import net.minecraft.server.level.ServerLevel

object TeleportQueueLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(TELEPORT_QUEUE_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(TELEPORT_QUEUE_DATA_ATTACHMENT, data)
    }
}