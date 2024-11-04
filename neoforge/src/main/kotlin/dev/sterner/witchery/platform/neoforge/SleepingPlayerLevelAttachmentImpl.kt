package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.SLEEPING_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import net.minecraft.server.level.ServerLevel

object SleepingPlayerLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): SleepingPlayerLevelAttachment.Data {
        return level.getData(SLEEPING_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: SleepingPlayerLevelAttachment.Data) {
        level.setData(SLEEPING_PLAYER_DATA_ATTACHMENT, data)
    }

}