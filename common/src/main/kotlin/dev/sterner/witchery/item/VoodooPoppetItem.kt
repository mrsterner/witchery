package dev.sterner.witchery.item

import dev.sterner.witchery.handler.PoppetHandler
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext

class VoodooPoppetItem(properties: Properties) : PoppetItem(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val item = context.itemInHand
        val player = context.player

        if (player != null) {
            val blockHitResult = getPlayerPOVHitResult(
                level,
                player,
                ClipContext.Fluid.SOURCE_ONLY
            )
            return PoppetHandler.handleUseVoodoo(level, pos, item, player, blockHitResult)
        }

        return InteractionResult.PASS
    }
}