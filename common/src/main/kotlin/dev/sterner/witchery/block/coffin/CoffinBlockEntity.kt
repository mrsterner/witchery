package dev.sterner.witchery.block.coffin

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import kotlin.math.abs
import kotlin.math.pow


class CoffinBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.COFFIN.get(), blockPos, blockState) {

    private var openProgress: Float = 0f
    private var targetOpenProgress: Float = 0f
    private val maxAngle: Float = 45f

    private val openLerpFactor: Float = 0.15f
    private val closeLerpFactor: Float = 0.15f

    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        super.tick(level, pos, blockState)
        
        if (!level.isClientSide) return

        val isOpen = blockState.getValue(CoffinBlock.OPEN)
        targetOpenProgress = if (isOpen) maxAngle else 0f

        val previousOpenProgress = openProgress
        openProgress = lerpAngle(openProgress, targetOpenProgress, if (isOpen) openLerpFactor else closeLerpFactor)

        if (abs(previousOpenProgress - openProgress) > 0.01f) {
            setChanged()
        }
    }
    
    fun getEasedOpenProgress(): Float = easeInOut(openProgress)

    private fun lerpAngle(current: Float, target: Float, factor: Float): Float {
        return current + factor * (target - current)
    }

    private fun easeInOut(x: Float): Float {
        val v = 45
        val c = -v / 2
        val g = -2
        val s = 16
        return (v / (1 + 10.0.pow((g * (c + x) / s).toDouble()))).toFloat()
    }
}