package dev.sterner.witchery.content.block.ancient_tablet


import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class AncientTabletBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.ANCIENT_SLATE.get(), AncientTabletBlock.STRUCTURE.get(), blockPos, blockState) {

    private var tabletId: UUID = UUID.randomUUID()

    fun getTabletId(): UUID {
        return tabletId
    }

    override fun tick(
        level: Level,
        pos: BlockPos,
        blockState: BlockState
    ) {
        super.tick(level, pos, blockState)
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