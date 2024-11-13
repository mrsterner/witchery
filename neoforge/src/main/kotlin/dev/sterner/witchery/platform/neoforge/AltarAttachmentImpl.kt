package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

object AltarAttachmentImpl {

    @JvmStatic
    fun setAltarPos(level: ServerLevel, pos: BlockPos) {
        if (!level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT).altarSet.contains(pos)) {
            val data = level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT)
            data.altarSet.add(pos)
            level.setData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT, data)
        }
    }

    @JvmStatic
    fun removeAltarPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT)
        data.altarSet.remove(pos)
        level.setData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun getAltarPos(level: ServerLevel): MutableSet<BlockPos> {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT).altarSet
    }
}