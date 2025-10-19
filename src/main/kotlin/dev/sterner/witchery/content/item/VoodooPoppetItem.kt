package dev.sterner.witchery.content.item

import dev.sterner.witchery.features.poppet.VoodooPoppet
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
            return VoodooPoppet.handleInteraction(level, pos, item, blockHitResult)
        }

        return InteractionResult.PASS
    }
}