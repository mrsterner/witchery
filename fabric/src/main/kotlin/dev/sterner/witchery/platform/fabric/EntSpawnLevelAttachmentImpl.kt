package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.WitcheryFabric
import dev.sterner.witchery.platform.EntSpawnLevelAttachment.Data
import net.minecraft.server.level.ServerLevel

object EntSpawnLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getAttachedOrCreate(WitcheryFabric.ENT_DATA_TYPE)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setAttached(WitcheryFabric.ENT_DATA_TYPE, data)
    }
}