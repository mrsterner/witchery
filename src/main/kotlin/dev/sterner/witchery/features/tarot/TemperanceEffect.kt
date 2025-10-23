package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.content.item.brew.BrewItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PotionItem

class TemperanceEffect : TarotEffect(15) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.temperance.reversed" else "tarot.witchery.temperance"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.temperance.reversed.description" else "tarot.witchery.temperance.description"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (player.health < player.maxHealth * 0.5f && player.level().gameTime % 40 == 0L) {
                player.heal(0.5f)
            }
        }
    }

    override fun onItemUse(player: Player, item: ItemStack, isReversed: Boolean) {
        if (!isReversed && (item.item is PotionItem || item.item is BrewItem)) {
            //TODO Potion effects last longer
            player.displayClientMessage(
                Component.literal("Not implemented yet :(").withStyle(ChatFormatting.AQUA),
                true
            )
        }
    }

    override fun onEnterWater(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.addEffect(MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0))
        }
    }
}