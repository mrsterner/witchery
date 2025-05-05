package dev.sterner.witchery.item

import dev.sterner.witchery.handler.ChainManager
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DebugWand(properties: Properties) : Item(properties) {

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {

        ChainManager.createChain(
            level = player.level(),
            position = player.position().add(5.0,.0, 5.0),
            targetEntity = interactionTarget,
            lifetime = 20 * 60
        )

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand)
    }
}