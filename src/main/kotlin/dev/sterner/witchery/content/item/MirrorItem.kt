package dev.sterner.witchery.content.item


import dev.sterner.witchery.content.block.mirror.MirrorBlock
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity
import dev.sterner.witchery.core.api.multiblock.MultiBlockItem
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import java.awt.Color
import java.util.UUID


class MirrorItem(
    block: Block,
    properties: Properties
) : MultiBlockItem(block, properties, MirrorBlock.STRUCTURE) {

    override fun canPlace(context: BlockPlaceContext, state: BlockState): Boolean {
        val isShiftClick = context.player?.isShiftKeyDown ?: false

        if (isShiftClick) {
            return canSuperPlace(context, state)
        }

        return super.canPlace(context, state)
    }

    private fun canSuperPlace(context: BlockPlaceContext, state: BlockState): Boolean {
        val player = context.player
        val collisionContext = if (player == null) CollisionContext.empty() else CollisionContext.of(player)
        return (!this.mustSurvive() || state.canSurvive(context.level, context.clickedPos))
                && context.level.isUnobstructed(state, context.clickedPos, collisionContext)
    }

    override fun placeBlock(context: BlockPlaceContext, state: BlockState): Boolean {
        val isShiftClick = context.player?.isShiftKeyDown ?: false
        val stack = context.itemInHand

        val success = if (isShiftClick) {
            val v = context.level.setBlock(context.clickedPos, state, 3)
            val be = context.level.getBlockEntity(context.clickedPos)
            if (be != null && be is MirrorBlockEntity) {
                be.isSmallMirror = true
                be.setChanged()
            }
            v
        } else {
            super.placeBlock(context, state)
        }

        if (success) {
            val pairId = getPairId(stack)
            if (pairId != null) {
                val entity = context.level.getBlockEntity(context.clickedPos) as? MirrorBlockEntity
                entity?.putPairId(pairId)

                context.player?.displayClientMessage(
                    Component.literal("Mirror linked to its pair!"),
                    true
                )
            }
        }

        return success
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val pairId = getPairId(stack)
        if (pairId != null) {
            tooltipComponents.add(
                Component.literal("Paired Mirror").withColor(0xAA00FF)
            )

            tooltipComponents.add(
                Component.literal("Pair ID: ").withColor(0xFFAA00)
                    .append(Component.literal(pairId.toString().substring(0, 8) + "...").withColor(0x55FFFF))
            )
        } else {
            tooltipComponents.add(
                Component.literal("Unpaired Mirror").withColor(0xFF5555)
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    companion object {
        fun getPairId(stack: ItemStack): UUID? {
            return stack.get(WitcheryDataComponents.MIRROR_PAIR_ID.get())
        }

        fun setPairId(stack: ItemStack, pairId: UUID) {
            stack.set(WitcheryDataComponents.MIRROR_PAIR_ID.get(), pairId)
        }
    }
}
