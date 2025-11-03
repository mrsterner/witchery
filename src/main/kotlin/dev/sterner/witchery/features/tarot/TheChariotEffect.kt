package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.Witchery
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

class TheChariotEffect : TarotEffect(8) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_chariot.reversed" else "tarot.witchery.the_chariot"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_chariot.reversed.description" else "tarot.witchery.the_chariot.description"
    )

    override fun onAdded(player: Player, isReversed: Boolean) {
        updateSpeedModifier(player, isReversed)
    }

    override fun onRemoved(player: Player, isReversed: Boolean) {
        player.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(CHARIOT_SPEED_MODIFIER_ID)
    }

    private fun updateSpeedModifier(player: Player, isReversed: Boolean) {
        val speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED) ?: return

        speedAttribute.removeModifier(CHARIOT_SPEED_MODIFIER_ID)

        val modifier = AttributeModifier(
            CHARIOT_SPEED_MODIFIER_ID,
            if (isReversed) -0.05 else 0.1,
            AttributeModifier.Operation.ADD_VALUE
        )
        speedAttribute.addPermanentModifier(modifier)
    }

    companion object {
        private val CHARIOT_SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
            Witchery.MODID,
            "chariot_speed_modifier"
        )
    }
}