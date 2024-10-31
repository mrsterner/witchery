package dev.sterner.witchery.platform.poppet.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.poppet.PoppetData
import net.minecraft.server.level.ServerLevel

object PoppetDataAttachmentImpl {

    @JvmStatic
    fun getPoppetData(level: ServerLevel): PoppetData {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.POPPET_DATA_TYPE)
    }

    @JvmStatic
    fun setPoppetData(level: ServerLevel, data: PoppetData) {
        level.setAttached(WitcheryFabricAttachmentRegistry.POPPET_DATA_TYPE, data)
    }
}