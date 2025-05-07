package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.NecromancerLevelAttachment
import net.minecraft.server.level.ServerLevel

object NecromancerLevelAttachmentImpl {
    @JvmStatic
    fun getData(level: ServerLevel): NecromancerLevelAttachment.NecroList {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.NECRO_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: NecromancerLevelAttachment.NecroList) {
        level.setData(WitcheryNeoForgeAttachmentRegistry.NECRO_DATA_ATTACHMENT, data)
    }
}