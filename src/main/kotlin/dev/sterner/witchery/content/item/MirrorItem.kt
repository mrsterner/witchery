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
            context.level.setBlock(context.clickedPos, state, 3)
        } else {
            super.placeBlock(context, state)
        }

        if (success) {
            val linkedPos = getLinkedPos(stack)
            if (linkedPos != null) {
                val entity = context.level.getBlockEntity(context.clickedPos) as? MirrorBlockEntity
                entity?.linkToMirror(linkedPos)

                context.player?.displayClientMessage(
                    Component.literal("Mirror linked to waystone location!"),
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
        val linkedPos = getLinkedPos(stack)
        if (linkedPos != null) {
            val dimension = WaystoneItem.capitalizeString(linkedPos.dimension().location().path)
            val color = when (dimension) {
                "The Nether" -> Color(255, 0, 0).rgb
                "The End" -> Color(255, 0, 255).rgb
                else -> Color(0, 255, 0).rgb
            }

            tooltipComponents.add(
                Component.literal("Linked to: ").withColor(0xFFAA00)
                    .append(Component.literal(dimension).withColor(color))
            )

            tooltipComponents.add(
                Component.literal("Position: ").withColor(0xFFAA00)
                    .append(Component.literal("${linkedPos.pos().x} ${linkedPos.pos().y} ${linkedPos.pos().z}").withColor(0x55FFFF))
            )
        } else {
            tooltipComponents.add(
                Component.literal("Not linked").withColor(0xFF5555)
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    companion object {
        fun getLinkedPos(stack: ItemStack): GlobalPos? {
            return stack.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())
        }

        fun setLinkedPos(stack: ItemStack, pos: GlobalPos) {
            stack.set(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get(), pos)
        }
    }
}
