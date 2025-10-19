package dev.sterner.witchery.item

import dev.sterner.witchery.features.chain.ChainManager
import dev.sterner.witchery.features.chain.ChainType
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
            if (player.isShiftKeyDown) {
                ChainManager.createHookAndPullChain(
                    player.level(),
                    player.position().add(0.0, 0.0, 7.0),
                    targetEntity,
                    0.15f, // Pull strength
                    0.15f,  // Fast extension speed
                    chainType = ChainType.SOUL
                )
            } else {
                val chains = ChainManager.createMultipleChains(
                    level,
                    targetEntity,
                    5,
                    8.0,
                    3,
                    160,
                    true,
                    chainType = ChainType.SPIRIT
                )
                player.displayClientMessage(
                    Component.literal("Created ${chains.size} chains around the target!").setStyle(
                        Style.EMPTY.withColor(Color.GREEN.rgb)
                    ),
                    true
                )
            }
        }

        return InteractionResult.SUCCESS
    }
}