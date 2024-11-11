package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.FamiliarLevelAttachment
import net.minecraft.server.level.ServerLevel
import java.util.*

object FamiliarLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): FamiliarLevelAttachment.Data {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.FAMILIAR_LEVEL_DATA_TYPE)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data:  FamiliarLevelAttachment.Data) {
        level.setAttached(WitcheryFabricAttachmentRegistry.FAMILIAR_LEVEL_DATA_TYPE, data)
    }
}