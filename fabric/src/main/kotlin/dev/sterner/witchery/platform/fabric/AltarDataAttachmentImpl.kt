package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

@Suppress("UnstableApiUsage")
object AltarDataAttachmentImpl {

    @JvmStatic
    fun setAltarPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.ALTAR_LEVEL_DATA_TYPE)
        if (!data.altarSet.contains(pos)) {
            data.altarSet.add(pos)
            level.setAttached(WitcheryFabricAttachmentRegistry.ALTAR_LEVEL_DATA_TYPE, data)
        }
    }

    @JvmStatic
    fun removeAltarPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.ALTAR_LEVEL_DATA_TYPE)
        data.altarSet.remove(pos)
        level.setAttached(WitcheryFabricAttachmentRegistry.ALTAR_LEVEL_DATA_TYPE, data)
    }

    @JvmStatic
    fun getAltarPos(level: ServerLevel): MutableSet<BlockPos> {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.ALTAR_LEVEL_DATA_TYPE).altarSet
    }
}