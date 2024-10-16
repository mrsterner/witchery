package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

object AltarDataAttachmentPlatform {

    @JvmStatic
    @ExpectPlatform
    fun setAltarPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun removeAltarPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getAltarPos(level: ServerLevel): MutableSet<BlockPos> {
        throw AssertionError()
    }
}