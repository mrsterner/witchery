package dev.sterner.witchery.platform.poppet.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.poppet.PoppetLevelAttachment
import net.minecraft.server.level.ServerLevel

object PoppetLevelAttachmentImpl {

    @JvmStatic
    fun getPoppetData(level: ServerLevel): PoppetLevelAttachment.PoppetData {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.POPPET_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setPoppetData(level: ServerLevel, data: PoppetLevelAttachment.PoppetData) {
        level.setData(WitcheryNeoForgeAttachmentRegistry.POPPET_DATA_ATTACHMENT, data)
    }
}