package dev.sterner.witchery.content.block.phylactery


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.AbstractCandleBlock.LIT
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LanternBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.ToIntFunction

class PhylacteryBlock(properties: Properties) : WitcheryBaseEntityBlock(
    properties.lightLevel(
        litBlockEmission(8)
    )
), SimpleWaterloggedBlock {

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LanternBlock.WATERLOGGED, false)
                .setValue(LIT, false)
                .setValue(VARIANT, Variant.GOLD)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(LanternBlock.WATERLOGGED, LIT, VARIANT)
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        val item = WitcheryItems.PHYLACTERY.get().defaultInstance
        item.set(WitcheryDataComponents.PHYLACTERY_VARIANT.get(), state.getValue(VARIANT))
        return item
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return PhylacteryBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0)
    }

    enum class Variant : StringRepresentable {
        GOLD,
        SOUL;

        override fun getSerializedName(): String {
            return name.lowercase()
        }
    }

    companion object {

        var VARIANT = EnumProperty.create("variant", Variant::class.java)

        fun litBlockEmission(lightValue: Int): ToIntFunction<BlockState> {
            return ToIntFunction { blockState: BlockState ->
                if (blockState.getValue(
                        LIT
                    ) == true
                ) lightValue else 0
            }
        }
    }
}