package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.EntSpawnLevelAttachment.Data
import net.minecraft.server.level.ServerLevel

object EntSpawnLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.ENT_DATA_TYPE)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setAttached(WitcheryFabricAttachmentRegistry.ENT_DATA_TYPE, data)
    }
}