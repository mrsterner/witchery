package dev.sterner.witchery.block.ancient_tablet

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import java.util.Optional
import java.util.UUID

class AncientTabletBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.ANCIENT_SLATE.get(), blockPos, blockState) {

    private var tabletId: UUID = UUID.randomUUID()

    fun getTabletId(): UUID {
        return tabletId;
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