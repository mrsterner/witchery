package dev.sterner.witchery.features.affliction.werewolf


import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.event.AfflictionClientEventHandler
import dev.sterner.witchery.features.affliction.AfflictionTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics


object WerewolfClientSpecificEventHandler {

    @JvmStatic
    fun renderHud(guiGraphics: GuiGraphics) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val isNotWere = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LYCANTHROPY) <= 0
        if (isNotWere) return

        val hasOffhand = !player.offhandItem.isEmpty
        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5 - if (hasOffhand) 32 else 0

        AfflictionClientEventHandler.drawAbilityBar(guiGraphics, player, x, y)
    }

}