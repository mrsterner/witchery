package dev.sterner.witchery.block.spirit_portal

import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class SpiritPortalBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.SPIRIT_PORTAL.get(), SpiritPortalBlock.STRUCTURE.get(), blockPos, blockState) {

    var isOpening: Boolean = false
    private var lastProgress = 0.0f
    var progress = 0.0f

    fun easeIn(t: Float): Float {
        return t * t
    }

    fun easeOut(t: Float): Float {
        return t * (2 - t)
    }

    fun getRenderProgress(partialTick: Float): Float {
        val interpolatedProgress = Mth.lerp(partialTick, lastProgress, progress)

        return if (isOpening) {
            easeOut(interpolatedProgress)
        } else {
            easeIn(interpolatedProgress)
        }
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (progress != 0f && progress != 1f) {
            return InteractionResult.FAIL
        }

        isOpening = !isOpening

        progress = if (isOpening) 0f else 1f
        setChanged()

        return InteractionResult.SUCCESS
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        if (!level.isClientSide) {
            val deltaTime = Minecraft.getInstance().timer.gameTimeDeltaTicks
            val progressChangeSpeed = 0.2f

            val previousProgress = progress
            progress = if (isOpening) {
                (progress + deltaTime * progressChangeSpeed).coerceAtMost(1f)
            } else {
                (progress - deltaTime * progressChangeSpeed).coerceAtLeast(0f)
            }

            if (previousProgress != progress) {
                lastProgress = previousProgress
                setChanged()
            }
        }
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        progress = pTag.getFloat("Progress")
        isOpening = pTag.getBoolean("IsOpening")
        lastProgress = progress
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putFloat("Progress", progress)
        tag.putBoolean("IsOpening", isOpening)
    }
}