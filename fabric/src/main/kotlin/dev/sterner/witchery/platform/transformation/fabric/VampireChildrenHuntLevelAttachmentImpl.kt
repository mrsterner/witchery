package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment
import net.minecraft.server.level.ServerLevel

object VampireChildrenHuntLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): VampireChildrenHuntLevelAttachment.Data {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: VampireChildrenHuntLevelAttachment.Data) {
        level.setAttached(WitcheryFabricAttachmentRegistry.VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT, data)
    }
}