package dev.sterner.witchery.content.block.mushroom_log

import dev.sterner.witchery.core.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.core.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.core.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import java.util.function.Supplier

class MushroomLogBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return MushroomLogBlockEntity(pos, state)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        level.destroyBlock(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING)), false)
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    companion object {

        fun makeMushroomLog(
            event: PlayerInteractEvent.RightClickBlock,
            player: Player,
            pos: BlockPos
        ) {
            val level = player.level()

            val state = level.getBlockState(pos)

            if (player.mainHandItem.`is`(Items.MOSS_BLOCK) && player.offhandItem.`is`(WitcheryTags.MUSHROOMS) && state.`is`(
                    Blocks.DARK_OAK_LOG
                )
            ) {
                val horizontalDirections = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)

                for (checkDir in horizontalDirections) {
                    val checkPos = pos.relative(checkDir)
                    val checkState = level.getBlockState(checkPos)

                    if (checkState.`is`(Blocks.DARK_OAK_LOG)) {

                        val canCreate = when (checkDir) {
                            Direction.NORTH, Direction.SOUTH -> {
                                val bl = state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Z
                                val bl2 = checkState.getValue(BlockStateProperties.AXIS) == Direction.Axis.Z
                                bl && bl2
                            }

                            Direction.EAST, Direction.WEST -> {
                                val bl = state.getValue(BlockStateProperties.AXIS) == Direction.Axis.X
                                val bl2 = checkState.getValue(BlockStateProperties.AXIS) == Direction.Axis.X
                                bl && bl2
                            }

                            else -> false
                        }

                        if (!canCreate) {
                            return
                        }

                        val coreDirection = checkDir
                        val componentDirection = checkDir.opposite

                        STRUCTURE.get().placeNoContext(level, checkPos, componentDirection)

                        level.setBlockAndUpdate(
                            pos,
                            WitcheryBlocks.MUSHROOM_LOG.get().defaultBlockState()
                                .setValue(BlockStateProperties.HORIZONTAL_FACING, coreDirection)
                        )

                        if (level.getBlockEntity(pos) is MushroomLogBlockEntity) {
                            (level.getBlockEntity(pos) as MushroomLogBlockEntity).setMushroom(player.offhandItem.copy())
                        }

                        if (level.getBlockEntity(checkPos) is MultiBlockComponentBlockEntity) {
                            (level.getBlockEntity(checkPos) as MultiBlockComponentBlockEntity).corePos = pos
                        }

                        if (!player.isCreative) {
                            player.mainHandItem.shrink(1)
                            player.offhandItem.shrink(1)
                        }
                        event.isCanceled = true
                        return
                    }
                }
            }
        }

        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        0,
                        0,
                        WitcheryBlocks.MUSHROOM_LOG_COMPONENT.get().defaultBlockState()
                    )
                ))
            }

    }
}