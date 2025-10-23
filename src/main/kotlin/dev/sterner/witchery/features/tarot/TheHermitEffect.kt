package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.Witchery
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

class TheHermitEffect : TarotEffect(10) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_hermit.reversed" else "tarot.witchery.the_hermit"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_hermit.reversed.description" else "tarot.witchery.the_hermit.description"
    )

    override fun onAdded(player: Player, isReversed: Boolean) {
        if (isReversed) {
            updateHealthModifier(player, true)
        }
    }

    override fun onRemoved(player: Player, isReversed: Boolean) {
        if (isReversed) {
            player.getAttribute(Attributes.MAX_HEALTH)?.removeModifier(HERMIT_HEALTH_MODIFIER_ID)
        }
    }

    override fun onTick(player: Player, isReversed: Boolean) {
        val nearbyPlayers = player.level().players().count {
            it != player && it.distanceTo(player) < 32.0
        }

        if (isReversed) {
            if (player.level().gameTime % 20 == 0L) {
                updateHealthModifier(player, nearbyPlayers == 0)
            }
        } else {
            if (nearbyPlayers == 0 && player.level().gameTime % 100 == 0L) {
                player.giveExperiencePoints(1)
            }
        }
    }

    private fun updateHealthModifier(player: Player, isAlone: Boolean) {
        val healthAttribute = player.getAttribute(Attributes.MAX_HEALTH) ?: return

        healthAttribute.removeModifier(HERMIT_HEALTH_MODIFIER_ID)

        if (isAlone) {
            val healthModifier = AttributeModifier(
                HERMIT_HEALTH_MODIFIER_ID,
                -4.0,
                AttributeModifier.Operation.ADD_VALUE
            )
            healthAttribute.addTransientModifier(healthModifier)
        }
    }

    companion object {
        private val HERMIT_HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
            Witchery.MODID,
            "hermit_isolation_debuff"
        )
    }
}