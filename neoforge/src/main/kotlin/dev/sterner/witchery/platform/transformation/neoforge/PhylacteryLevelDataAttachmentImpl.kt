package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.transformation.PhylacteryLevelDataAttachment
import net.minecraft.server.level.ServerLevel

object PhylacteryLevelDataAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): PhylacteryLevelDataAttachment.Data {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.PHYLACTERY_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: PhylacteryLevelDataAttachment.Data) {
        level.setData(WitcheryNeoForgeAttachmentRegistry.PHYLACTERY_LEVEL_DATA_ATTACHMENT, data)
    }
}