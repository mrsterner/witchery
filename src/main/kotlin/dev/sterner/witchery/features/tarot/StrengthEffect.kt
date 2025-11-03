package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.Witchery
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

class StrengthEffect : TarotEffect(9) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.strength.reversed" else "tarot.witchery.strength"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.strength.reversed.description" else "tarot.witchery.strength.description"
    )

    override fun onAdded(player: Player, isReversed: Boolean) {
        updateDamageModifier(player, isReversed)
    }

    override fun onRemoved(player: Player, isReversed: Boolean) {
        player.getAttribute(Attributes.ATTACK_DAMAGE)?.removeModifier(STRENGTH_DAMAGE_MODIFIER_ID)
    }

    override fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {
        if (!isReversed) {
            player.heal(2f)
        }
    }

    private fun updateDamageModifier(player: Player, isReversed: Boolean) {
        val damageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE) ?: return

        damageAttribute.removeModifier(STRENGTH_DAMAGE_MODIFIER_ID)

        val modifier = AttributeModifier(
            STRENGTH_DAMAGE_MODIFIER_ID,
            if (isReversed) -2.0 else 3.0,
            AttributeModifier.Operation.ADD_VALUE
        )
        damageAttribute.addPermanentModifier(modifier)
    }

    companion object {
        private val STRENGTH_DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
            Witchery.MODID,
            "strength_damage_modifier"
        )
    }
}