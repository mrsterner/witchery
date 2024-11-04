package dev.sterner.witchery.platform.poppet.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.poppet.PoppetData
import net.minecraft.server.level.ServerLevel

object PoppetDataAttachmentImpl {

    @JvmStatic
    fun getPoppetData(level: ServerLevel): PoppetData {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.POPPET_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setPoppetData(level: ServerLevel, data: PoppetData) {
        level.setData(WitcheryNeoForgeAttachmentRegistry.POPPET_DATA_ATTACHMENT, data)
    }
}