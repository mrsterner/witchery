package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.FAMILIAR_LEVEL_DATA_ATTACHMENT
import dev.sterner.witchery.platform.FamiliarLevelAttachment
import net.minecraft.server.level.ServerLevel

object FamiliarLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): FamiliarLevelAttachment.Data {
        return level.getData(FAMILIAR_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data:  FamiliarLevelAttachment.Data) {
        level.setData(FAMILIAR_LEVEL_DATA_ATTACHMENT, data)
    }
}