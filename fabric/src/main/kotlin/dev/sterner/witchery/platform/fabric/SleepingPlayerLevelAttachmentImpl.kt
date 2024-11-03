package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry.SLEEPING_PLAYER_DATA_TYPE
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import net.minecraft.server.level.ServerLevel

object SleepingPlayerLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): SleepingPlayerLevelAttachment.Data {
        return level.getAttachedOrCreate(SLEEPING_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: SleepingPlayerLevelAttachment.Data) {
        level.setAttached(SLEEPING_PLAYER_DATA_TYPE, data)
    }

}