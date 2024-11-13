package dev.sterner.witchery.platform.poppet.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.poppet.PoppetLevelAttachment
import net.minecraft.server.level.ServerLevel

object PoppetLevelAttachmentImpl {

    @JvmStatic
    fun getPoppetData(level: ServerLevel): PoppetLevelAttachment.PoppetData {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.POPPET_DATA_TYPE)
    }

    @JvmStatic
    fun setPoppetData(level: ServerLevel, data: PoppetLevelAttachment.PoppetData) {
        level.setAttached(WitcheryFabricAttachmentRegistry.POPPET_DATA_TYPE, data)
    }
}