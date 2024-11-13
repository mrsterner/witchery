package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.SLEEPING_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.SleepingLevelAttachment
import net.minecraft.server.level.ServerLevel

object SleepingLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): SleepingLevelAttachment.Data {
        return level.getData(SLEEPING_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: SleepingLevelAttachment.Data) {
        level.setData(SLEEPING_PLAYER_DATA_ATTACHMENT, data)
    }
}