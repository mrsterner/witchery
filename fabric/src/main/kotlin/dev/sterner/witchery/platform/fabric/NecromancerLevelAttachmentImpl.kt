package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.NecromancerLevelAttachment
import net.minecraft.server.level.ServerLevel

object NecromancerLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): NecromancerLevelAttachment.NecroList {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.NECRO_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: NecromancerLevelAttachment.NecroList) {
        level.setAttached(WitcheryFabricAttachmentRegistry.NECRO_DATA_ATTACHMENT, data)
    }
}