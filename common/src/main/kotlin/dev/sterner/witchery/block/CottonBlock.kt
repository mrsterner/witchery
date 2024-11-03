package dev.sterner.witchery.block

import com.mojang.serialization.MapCodec
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.Containers
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class CottonBlock(properties: Properties) : BushBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(SPIRITED, false)
        )
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        val drop = if (level.dimension().location().path.equals("dream_world")) {
            WitcheryItems.WISPY_COTTON.get().defaultInstance
        } else if (level.dimension().location().path.equals("nightmare_world")){
            WitcheryItems.DISTURBED_COTTON.get().defaultInstance
        } else {
            null
        }

        if (drop != null) {
            Containers.dropItemStack(level, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, drop)
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        if (state.getValue(SPIRITED) && !level.dimensionType().hasFixedTime()) {
            return false
        }

        return super.canSurvive(state, level, pos)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder.add(SPIRITED))
    }

    override fun codec(): MapCodec<out BushBlock> {
        return CODEC
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val vec3 = state.getOffset(level, pos)
        return SHAPE.move(vec3.x, vec3.y, vec3.z)
    }

    companion object {
        val SPIRITED = BooleanProperty.create("spirited")
        val CODEC: MapCodec<CottonBlock> = simpleCodec { arg: Properties ->
            CottonBlock(
                arg
            )
        }
        val SHAPE: VoxelShape = box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
    }
}