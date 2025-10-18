package dev.sterner.witchery.api.multiblock

import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties


open class MultiBlockCoreEntity(
    type: BlockEntityType<*>,
    override val structure: MultiBlockStructure,
    pos: BlockPos,
    state: BlockState
) : WitcheryBaseBlockEntity(type, pos, state), IMultiBlockCore {

    override var componentPositions: ArrayList<BlockPos?> = ArrayList()
    var structureOffset: Vec3i = Vec3i.ZERO

    init {
        this.setupMultiBlock(
            pos,
            if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
                state.getValue(BlockStateProperties.HORIZONTAL_FACING)
            else null
        )
    }

    override fun onBreak(player: Player) {
        this.destroyMultiBlock(player, level!!, worldPosition)
        super.onBreak(player)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("OffsetX", structureOffset.x)
        tag.putInt("OffsetY", structureOffset.y)
        tag.putInt("OffsetZ", structureOffset.z)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        structureOffset = Vec3i(
            tag.getInt("OffsetX"),
            tag.getInt("OffsetY"),
            tag.getInt("OffsetZ")
        )
    }
}