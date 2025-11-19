package dev.sterner.witchery.content.block.ancient_tablet


import dev.sterner.witchery.core.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class AncientTabletBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.ANCIENT_SLATE.get(), AncientTabletBlock.STRUCTURE.get(), blockPos, blockState) {

    private var tabletId: UUID = UUID.randomUUID()

    var clientProgress = 0f
    var glowAlpha = 0f
        private set
    private var glowHoldTime = 0
    private var isFadingOut = false

    fun getTabletId(): UUID {
        return tabletId
    }

    fun updateGlowProgress(progress: Float) {
        if (progress > clientProgress) {
            clientProgress = progress
            glowAlpha = progress
            glowHoldTime = 100
            isFadingOut = false
        }
    }


    fun resetGlow() {
        clientProgress = 0f
        isFadingOut = true
    }

    override fun tick(
        level: Level,
        pos: BlockPos,
        blockState: BlockState
    ) {
        super.tick(level, pos, blockState)

        if (level.isClientSide) {
            if (glowHoldTime > 0) {
                glowHoldTime--
                if (glowHoldTime == 0) {
                    isFadingOut = true
                }
            }

            if (isFadingOut && glowAlpha > 0f) {
                glowAlpha = 0f.coerceAtLeast(glowAlpha - 0.01f)
                if (glowAlpha <= 0f) {
                    isFadingOut = false
                }
            }
        }
    }


    override fun saveAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider
    ) {
        super.saveAdditional(tag, registries)
        tag.putUUID("tabletId", tabletId)
    }

    override fun loadAdditional(
        pTag: CompoundTag,
        pRegistries: HolderLookup.Provider
    ) {
        super.loadAdditional(pTag, pRegistries)
        if (pTag.hasUUID("tabletId")) {
            tabletId = pTag.getUUID("tabletId")
        }
    }
}