package dev.sterner.witchery.block.blood_poppy


import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class BloodPoppyBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.BLOODY_POPPY.get(), blockPos, blockState) {
    var uuid: UUID? = null

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        uuid?.let { uuid -> tag.putUUID("taglocked", uuid) }

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)

        if (pTag.contains("taglocked")) {
            uuid = pTag.getUUID("taglocked")
        }
    }
}