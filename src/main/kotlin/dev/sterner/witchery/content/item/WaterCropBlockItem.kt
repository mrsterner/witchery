package dev.sterner.witchery.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

class WaterCropBlockItem(private val cropBlock: Block, properties: Properties) :
    ItemNameBlockItem(cropBlock, properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player
        if (player != null) {
            val blockHitResult = getPlayerPOVHitResult(
                context.level,
                player,
                ClipContext.Fluid.SOURCE_ONLY
            )
            val blockPos = blockHitResult.blockPos
            val direction = blockHitResult.direction
            val blockPos2 = blockPos.relative(direction)
            if (level.getBlockState(blockPos).`is`(Blocks.WATER) && level.getBlockState(blockPos2).`is`(Blocks.AIR)) {
                context.level.setBlockAndUpdate(blockPos2, cropBlock.defaultBlockState())
                if (context.player != null && !context.player!!.isCreative) {
                    context.itemInHand.shrink(1)
                }
                return InteractionResult.SUCCESS
            }
        }

        return super.useOn(context)
    }
}