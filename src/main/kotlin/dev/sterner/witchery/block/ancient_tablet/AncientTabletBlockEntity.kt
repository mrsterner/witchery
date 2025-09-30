package dev.sterner.witchery.block.ancient_tablet


import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class AncientTabletBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.ANCIENT_SLATE.get(), AncientTabletBlock.STRUCTURE.get(), blockPos, blockState) {

    private var tabletId: UUID = UUID.randomUUID()
    var textureId: Int = 1

    fun getTabletId(): UUID {
        return tabletId
    }

    override fun saveAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider
    ) {
        super.saveAdditional(tag, registries)
        tag.putUUID("tabletId", tabletId)
        tag.putInt("textureId", textureId)
    }

    override fun loadAdditional(
        pTag: CompoundTag,
        pRegistries: HolderLookup.Provider
    ) {
        super.loadAdditional(pTag, pRegistries)
        if (pTag.hasUUID("tabletId")) {
            tabletId = pTag.getUUID("tabletId")
        }
        if (pTag.hasUUID("textureId")) {
            textureId = pTag.getInt("textureId")
        }
    }
}