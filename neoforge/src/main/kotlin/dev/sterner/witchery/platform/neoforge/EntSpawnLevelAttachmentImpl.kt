package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForge
import dev.sterner.witchery.platform.EntSpawnLevelAttachment.Data
import net.minecraft.server.level.ServerLevel

object EntSpawnLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryNeoForge.ENT_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryNeoForge.ENT_DATA_ATTACHMENT, data)
    }
}