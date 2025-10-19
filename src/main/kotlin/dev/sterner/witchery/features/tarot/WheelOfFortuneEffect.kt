package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block

class WheelOfFortuneEffect : TarotEffect(11) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "Wheel of Fortune (Reversed)" else "Wheel of Fortune"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Misfortune follows" else "Fortune smiles upon you"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (!player.hasEffect(MobEffects.LUCK)) {
                player.addEffect(MobEffectInstance(MobEffects.LUCK, 400, 1, true, false))
            }
        } else {
            if (!player.hasEffect(MobEffects.UNLUCK)) {
                player.addEffect(MobEffectInstance(MobEffects.UNLUCK, 400, 1, true, false))
            }
        }
    }

    override fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {
        if (!isReversed && player.level().random.nextFloat() < 0.1f) {
            val rareDrop = when (player.level().random.nextInt(5)) {
                0 -> Items.IRON_INGOT
                1 -> Items.EMERALD
                2 -> Items.GOLD_INGOT
                3 -> WitcheryItems.MUTANDIS.get()
                else -> Items.EXPERIENCE_BOTTLE
            }
            Block.popResource(player.level(), entity.blockPosition(), ItemStack(rareDrop))
        }
    }
}