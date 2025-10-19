package dev.sterner.witchery.features.affliction.lich


import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.event.AfflictionClientEventHandler
import dev.sterner.witchery.features.affliction.AfflictionTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object LichdomClientSpecificEventHandler {

    @JvmStatic
    fun renderHud(guiGraphics: GuiGraphics) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val attach = AfflictionPlayerAttachment.getData(player)

        val isNotLich = attach.getLevel(AfflictionTypes.LICHDOM) <= 0 && !attach.isSoulForm()
        if (isNotLich) return

        val hasOffhand = !player.offhandItem.isEmpty
        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5 - if (hasOffhand) 32 else 0

        AfflictionClientEventHandler.drawAbilityBar(guiGraphics, player, x, y)
    }

}