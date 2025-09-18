package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.PhylacteryLevelDataAttachment
import net.minecraft.server.level.ServerLevel

object PhylacteryLevelDataAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): PhylacteryLevelDataAttachment.Data {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.PHYLACTERY_LEVEL_DATA_TYPE)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: PhylacteryLevelDataAttachment.Data) {
        level.setAttached(WitcheryFabricAttachmentRegistry.PHYLACTERY_LEVEL_DATA_TYPE, data)
    }
}