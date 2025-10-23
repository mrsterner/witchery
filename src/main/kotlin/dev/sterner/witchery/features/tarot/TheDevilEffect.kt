package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.Witchery
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

class TheDevilEffect : TarotEffect(16) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Devil (Reversed)" else "The Devil"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Slowly break free from curses and debuffs, but lose experience in the process"
        else "Devastating strength at the cost of your vitality - reduced max health for increased damage"
    )

    override fun onAdded(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            val healthModifier = AttributeModifier(
                DEVIL_HEALTH_MODIFIER_ID,
                -2.0,
                AttributeModifier.Operation.ADD_VALUE
            )
            player.getAttribute(Attributes.MAX_HEALTH)?.addTransientModifier(healthModifier)
        }
    }

    override fun onRemoved(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.getAttribute(Attributes.MAX_HEALTH)?.removeModifier(DEVIL_HEALTH_MODIFIER_ID)
        }
    }

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (!player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                player.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 1, true, false))
            }
        } else {
            if (player.level().gameTime % 600 == 0L) {
                val debuffs = player.activeEffects
                    .filter { !it.effect.value().isBeneficial }
                    .map { it.effect }
                debuffs.forEach { player.removeEffect(it) }
                player.giveExperiencePoints(-5)
            }
        }
    }

    companion object {
        private val DEVIL_HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "devil_health_reduction")
    }
}