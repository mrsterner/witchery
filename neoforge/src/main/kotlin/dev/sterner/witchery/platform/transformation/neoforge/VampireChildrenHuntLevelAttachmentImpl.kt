package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment
import net.minecraft.server.level.ServerLevel

object VampireChildrenHuntLevelAttachmentImpl {

    @JvmStatic
    fun getData(level: ServerLevel): VampireChildrenHuntLevelAttachment.Data {
        return level.getData(VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: VampireChildrenHuntLevelAttachment.Data) {
        level.setData(VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT, data)
    }
}