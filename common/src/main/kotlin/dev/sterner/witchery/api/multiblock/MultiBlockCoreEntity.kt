package dev.sterner.witchery.api.multiblock

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import net.minecraft.core.BlockPos
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

    init {
        this.setupMultiBlock(pos, state.getValue(BlockStateProperties.HORIZONTAL_FACING))
    }

    override fun onBreak(player: Player) {
        this.destroyMultiBlock(player, level!!, worldPosition)
        super.onBreak(player)
    }
}