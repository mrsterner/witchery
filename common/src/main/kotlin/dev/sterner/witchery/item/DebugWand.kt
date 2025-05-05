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
import net.minecraft.world.item.context.UseOnContext
import java.awt.Color

class DebugWand(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val pos = context.clickedPos.relative(context.clickedFace)
        context.itemInHand.set(WitcheryDataComponents.CHAIN_POS.get(), pos)
        context.player?.displayClientMessage(
            Component.literal("Chain loc start: $pos").setStyle(Style.EMPTY.withColor(
                Color.CYAN.rgb)
            ),
            true
        )
        return super.useOn(context)
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (stack.has(WitcheryDataComponents.CHAIN_POS.get())) {
            val pos = stack.get(WitcheryDataComponents.CHAIN_POS.get())

            ChainManager.createChain(
                level = player.level(),
                position = pos!!.center,
                targetEntity = interactionTarget,
                lifetime = 20 * 60
            )
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand)
    }
}