package dev.sterner.witchery.block

import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CrossCollisionBlock
import net.minecraft.world.level.block.PipeBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty

class AltarCreationBlock(properties: Properties) : Block(properties.noOcclusion()) {

    init {
        this.registerDefaultState(
            stateDefinition
                .any()
                .setValue(CrossCollisionBlock.NORTH, false)
                .setValue(CrossCollisionBlock.EAST, false)
                .setValue(CrossCollisionBlock.SOUTH, false)
                .setValue(CrossCollisionBlock.WEST, false)
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val blockGetter: BlockGetter = context.level
        val blockPos = context.clickedPos
        val blockPos2 = blockPos.north()
        val blockPos3 = blockPos.east()
        val blockPos4 = blockPos.south()
        val blockPos5 = blockPos.west()
        val blockState = blockGetter.getBlockState(blockPos2)
        val blockState2 = blockGetter.getBlockState(blockPos3)
        val blockState3 = blockGetter.getBlockState(blockPos4)
        val blockState4 = blockGetter.getBlockState(blockPos5)
        return super.getStateForPlacement(context)!!
            .setValue(CrossCollisionBlock.NORTH, this.connectsTo(blockState))
            .setValue(CrossCollisionBlock.EAST, this.connectsTo(blockState2))
            .setValue(CrossCollisionBlock.SOUTH, this.connectsTo(blockState3))
            .setValue(CrossCollisionBlock.WEST, this.connectsTo(blockState4))
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (direction.axis.plane == Direction.Plane.HORIZONTAL
        ) state.setValue(
            PROPERTY_BY_DIRECTION[direction]!!,
            this.connectsTo(
                neighborState
            )
        )
        else super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    private val PROPERTY_BY_DIRECTION: Map<Direction, BooleanProperty> = PipeBlock.PROPERTY_BY_DIRECTION
        .entries
        .stream()
        .filter { entry: Map.Entry<Direction, BooleanProperty?> ->
            entry.key.axis
                .isHorizontal
        }
        .collect(Util.toMap()) as Map<Direction, BooleanProperty>

    private fun connectsTo(state: BlockState): Boolean {
        val block = state.block
        val bl2 = block is AltarCreationBlock
        return bl2
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(
            CrossCollisionBlock.NORTH,
            CrossCollisionBlock.EAST,
            CrossCollisionBlock.WEST,
            CrossCollisionBlock.SOUTH
        )
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        val northBlockState = level.getBlockState(pos.north())
        val southBlockState = level.getBlockState(pos.south())
        val eastBlockState = level.getBlockState(pos.east())
        val westBlockState = level.getBlockState(pos.west())
        var list = mutableListOf<BlockState>(northBlockState, southBlockState, eastBlockState, westBlockState)
        list = list.filter { it.`is`(WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get()) }.toMutableList()

        if (list.size == 2
            && isAltar(northBlockState)
            && isAltar(eastBlockState)
        ) {

            val ne = level.getBlockState(pos.north().east())
            val nn = level.getBlockState(pos.north().north())
            val nne = level.getBlockState(pos.north().north().east())
            if (isAltar(ne) && isAltar(nn) && isAltar(nne)) {
                makeAltar(level, pos.north(), Direction.EAST)
            }
        }

        if (list.size == 2
            && isAltar(northBlockState)
            && isAltar(westBlockState)
        ) {

            val nw = level.getBlockState(pos.north().west())
            val nn = level.getBlockState(pos.north().north())
            val nnw = level.getBlockState(pos.north().north().west())
            if (isAltar(nw) && isAltar(nn) && isAltar(nnw)) {
                makeAltar(level, pos.north(), Direction.WEST)
            }
        }

        if (list.size == 2
            && isAltar(southBlockState)
            && isAltar(westBlockState)
        ) {

            val sw = level.getBlockState(pos.south().west())
            val ss = level.getBlockState(pos.south().north())
            val ssw = level.getBlockState(pos.south().south().west())
            if (isAltar(sw) && isAltar(ss) && isAltar(ssw)) {
                makeAltar(level, pos.south(), Direction.WEST)
            }
        }

        if (list.size == 2
            && isAltar(southBlockState)
            && isAltar(eastBlockState)
        ) {

            val se = level.getBlockState(pos.south().east())
            val ss = level.getBlockState(pos.south().north())
            val sse = level.getBlockState(pos.south().south().east())
            if (isAltar(se) && isAltar(ss) && isAltar(sse)) {
                makeAltar(level, pos.south(), Direction.EAST)
            }
        }

        //Middlehandleing
        if (list.size == 3 && isAltar(eastBlockState)) {
            val s = level.getBlockState(pos.south())
            val n = level.getBlockState(pos.north())
            val ne = level.getBlockState(pos.north().east())
            val se = level.getBlockState(pos.south().east())
            if (isAltar(s) && isAltar(n) && isAltar(ne) && isAltar(se)) {
                makeAltar(level, pos, Direction.EAST)
            }
        }

        if (list.size == 3 && isAltar(westBlockState)) {
            val s = level.getBlockState(pos.south())
            val n = level.getBlockState(pos.north())
            val nw = level.getBlockState(pos.north().west())
            val sw = level.getBlockState(pos.south().west())
            if (isAltar(s) && isAltar(n) && isAltar(nw) && isAltar(sw)) {
                makeAltar(level, pos, Direction.WEST)
            }
        }


        if (list.size == 2
            && isAltar(eastBlockState)
            && isAltar(southBlockState)
        ) {

            val ee = level.getBlockState(pos.east().east())
            val es = level.getBlockState(pos.east().south())
            val ees = level.getBlockState(pos.east().east().south())
            if (isAltar(ee) && isAltar(es) && isAltar(ees)) {
                makeAltar(level, pos.east(), Direction.SOUTH)
            }
        }

        if (list.size == 2
            && isAltar(eastBlockState)
            && isAltar(northBlockState)
        ) {

            val ee = level.getBlockState(pos.east().east())
            val en = level.getBlockState(pos.east().north())
            val een = level.getBlockState(pos.east().east().north())
            if (isAltar(ee) && isAltar(en) && isAltar(een)) {
                makeAltar(level, pos.east(), Direction.NORTH)
            }
        }

        if (list.size == 2
            && isAltar(westBlockState)
            && isAltar(southBlockState)
        ) {

            val ww = level.getBlockState(pos.west().west())
            val ws = level.getBlockState(pos.west().south())
            val wws = level.getBlockState(pos.west().west().south())
            if (isAltar(ww) && isAltar(ws) && isAltar(wws)) {
                makeAltar(level, pos.west(), Direction.SOUTH)
            }
        }

        if (list.size == 2
            && isAltar(westBlockState)
            && isAltar(northBlockState)
        ) {

            val ww = level.getBlockState(pos.west().west())
            val wn = level.getBlockState(pos.west().north())
            val wwn = level.getBlockState(pos.west().west().north())
            if (isAltar(ww) && isAltar(wn) && isAltar(wwn)) {
                makeAltar(level, pos.west(), Direction.NORTH)
            }
        }

        if (list.size == 3 && isAltar(southBlockState)) {
            val e = level.getBlockState(pos.east())
            val w = level.getBlockState(pos.west())
            val es = level.getBlockState(pos.east().south())
            val ws = level.getBlockState(pos.west().south())

            if (isAltar(e) && isAltar(w) && isAltar(es) && isAltar(ws)) {
                makeAltar(level, pos, Direction.SOUTH)
            }
        }

        if (list.size == 3 && isAltar(northBlockState)) {
            val e = level.getBlockState(pos.east())
            val w = level.getBlockState(pos.west())
            val en = level.getBlockState(pos.east().north())
            val wn = level.getBlockState(pos.west().north())

            if (isAltar(e) && isAltar(w) && isAltar(en) && isAltar(wn)) {
                makeAltar(level, pos, Direction.NORTH)
            }
        }

        super.onPlace(state, level, pos, oldState, movedByPiston)
    }

    private fun makeAltar(level: Level, pos: BlockPos, dire: Direction) {
        val corePosition = pos.relative(dire.opposite)
        AltarBlock.STRUCTURE.get().placeNoContext(level, pos, dire)
        level.setBlockAndUpdate(
            pos,
            WitcheryBlocks.ALTAR.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, dire.opposite)
        )

        if (level.getBlockEntity(corePosition) is MultiBlockComponentBlockEntity) {
            (level.getBlockEntity(corePosition) as MultiBlockComponentBlockEntity).corePos = corePosition
        }
    }

    fun isAltar(state: BlockState): Boolean {
        return state.`is`(WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get())
    }
}