package dev.sterner.witchery.item

import dev.sterner.witchery.item.TaglockItem.Companion.getLivingEntity
import dev.sterner.witchery.item.TaglockItem.Companion.getPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids

class VoodooPoppetItem(properties: Properties) : PoppetItem(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val item = context.itemInHand
        val player = context.player

        val blockHitResult = getPlayerPOVHitResult(
            level,
            player,
            ClipContext.Fluid.SOURCE_ONLY
        )

        if (level.getBlockState(blockHitResult.blockPos).`is`(Blocks.LAVA)) {
            val maybePlayer = getPlayer(level, item)
            val maybeEntity = getLivingEntity(level, item)
            if (maybePlayer != null || maybeEntity != null) {
                maybePlayer?.remainingFireTicks = 20 * 2
                maybeEntity?.remainingFireTicks = 20 * 2
                item.damageValue += 1
                if (item.damageValue >= item.maxDamage) {
                    item.shrink(1)
                }
                return InteractionResult.SUCCESS
            }
        }

        return super.useOn(context)
    }
}