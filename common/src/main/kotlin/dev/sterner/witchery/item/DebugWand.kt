package dev.sterner.witchery.item

import dev.sterner.witchery.handler.ChainManager
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.awt.Color

class DebugWand(properties: Properties) : Item(properties) {

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        targetEntity: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        val level = player.level()

        if (!level.isClientSide) {
            val chains = ChainManager.createMultipleChains(
                level = level,
                targetEntity = targetEntity,
                numChains = 5,
                radius = 8.0,
                lifetime = 20 * 20
            )

            player.displayClientMessage(
                Component.literal("Created ${chains.size} chains around the target!").setStyle(
                    Style.EMPTY.withColor(Color.GREEN.rgb)
                ),
                true
            )
        }

        return InteractionResult.SUCCESS
    }
}