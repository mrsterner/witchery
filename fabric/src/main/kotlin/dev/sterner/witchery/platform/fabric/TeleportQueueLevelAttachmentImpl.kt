package dev.sterner.witchery.platform.fabric


import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry.TELEPORT_QUEUE_DATA_ATTACHMENT
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment.Data
import net.minecraft.server.level.ServerLevel

object TeleportQueueLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getAttachedOrCreate(TELEPORT_QUEUE_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setAttached(TELEPORT_QUEUE_DATA_ATTACHMENT, data)
    }
}