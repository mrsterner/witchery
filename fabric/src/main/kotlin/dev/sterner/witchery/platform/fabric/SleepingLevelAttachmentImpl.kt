package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry.SLEEPING_PLAYER_DATA_TYPE
import dev.sterner.witchery.platform.SleepingLevelAttachment
import net.minecraft.server.level.ServerLevel

object SleepingLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): SleepingLevelAttachment.Data {
        return level.getAttachedOrCreate(SLEEPING_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: SleepingLevelAttachment.Data) {
        level.setAttached(SLEEPING_PLAYER_DATA_TYPE, data)
    }
}