package dev.sterner.witchery.block.mushroom_log

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.InteractionEvent
import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionHand
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
import java.util.function.Supplier

class MushroomLogBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape? {
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
        
        fun registerEvents() {
            InteractionEvent.RIGHT_CLICK_BLOCK.register(::makeMushroomLog)
        }

        fun makeMushroomLog(player: Player, hand: InteractionHand, pos: BlockPos, face: Direction): EventResult {
            val level = player.level()

            if (player.mainHandItem.`is`(Items.MOSS_BLOCK) && player.offhandItem.`is`(WitcheryTags.MUSHROOMS) && level.getBlockState(pos).`is`(Blocks.DARK_OAK_LOG)) {
                val horizontalDirections = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)

                for (checkDir in horizontalDirections) {
                    val checkPos = pos.relative(checkDir)
                    val checkState = level.getBlockState(checkPos)

                    if (checkState.`is`(Blocks.DARK_OAK_LOG)) {
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
                        }

                        return EventResult.interruptTrue()
                    }
                }
            }

            return EventResult.pass()
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