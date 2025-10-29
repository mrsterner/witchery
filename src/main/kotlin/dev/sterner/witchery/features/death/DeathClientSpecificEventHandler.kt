package dev.sterner.witchery.features.death

import dev.sterner.witchery.features.affliction.event.AfflictionClientEventHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object DeathClientSpecificEventHandler {

    @JvmStatic
    fun renderHud(guiGraphics: GuiGraphics) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val isDeath = DeathPlayerAttachment.getData(player).isDeath || DeathTransformationHelper.hasDeathBoots(player)
        if (!isDeath) return

        val hasOffhand = !player.offhandItem.isEmpty
        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5 - if (hasOffhand) 32 else 0

        AfflictionClientEventHandler.drawAbilityBar(guiGraphics, player, x, y)
    }

}