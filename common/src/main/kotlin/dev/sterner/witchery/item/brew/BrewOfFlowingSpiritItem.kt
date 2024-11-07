package dev.sterner.witchery.item.brew

import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class BrewOfFlowingSpiritItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return InteractionResultHolder.pass(player.mainHandItem)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val clickedPos = context.clickedPos
        val state = level.getBlockState(clickedPos)

        if (state.block is DoorBlock && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            val half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)
            val direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING)

            val belowPops = if (half == DoubleBlockHalf.UPPER) clickedPos.below() else clickedPos
            if (level.getBlockState(belowPops.east()).block is DoorBlock) {
                makePortal(level, belowPops, direction)
                return InteractionResult.SUCCESS_NO_ITEM_USED
            }
            if (level.getBlockState(belowPops.west()).block is DoorBlock) {
                makePortal(level, belowPops.west(), direction)
                return InteractionResult.SUCCESS_NO_ITEM_USED
            }
            if (level.getBlockState(belowPops.north()).block is DoorBlock) {
                makePortal(level, belowPops, direction)
                return InteractionResult.SUCCESS_NO_ITEM_USED
            }
            if (level.getBlockState(belowPops.south()).block is DoorBlock) {
                makePortal(level, belowPops, direction)
                return InteractionResult.SUCCESS_NO_ITEM_USED
            }
        }

        return super.useOn(context)
    }

    fun makePortal(level: Level, pos: BlockPos, direction: Direction) {

        SpiritPortalBlock.STRUCTURE.get().placeNoContext(level, pos, direction)

        // Set the Spirit Portal block at the given position, facing the opposite direction
        level.setBlockAndUpdate(
            pos,
            WitcheryBlocks.SPIRIT_PORTAL.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, direction.opposite)
        )

        // Set the core position in the MultiBlockComponentBlockEntity, if present
        if (level.getBlockEntity(pos) is MultiBlockComponentBlockEntity) {
            (level.getBlockEntity(pos) as MultiBlockComponentBlockEntity).corePos = pos
        }
    }

    override fun applyEffect(level: Level, livingEntity: LivingEntity?, result: HitResult) {
        var pos = BlockPos.containing(result.location)
        if (level.getBlockState(pos).canBeReplaced()) {
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        } else {
            if (result.type == HitResult.Type.BLOCK) {
                val blockHitResult = result as BlockHitResult
                pos = pos.relative(blockHitResult.direction)
            }
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        }
    }
}