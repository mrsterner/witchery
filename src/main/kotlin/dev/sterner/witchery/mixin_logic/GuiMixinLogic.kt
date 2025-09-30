package dev.sterner.witchery.mixin_logic


import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.util.RenderUtils
import dev.sterner.witchery.util.RenderUtils.innerRenderBlood
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.player.Player

object GuiMixinLogic {

    fun renderFoodLevel(player: Player, guiGraphics: GuiGraphics, y: Int, x: Int): Boolean {
        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0) {
            innerRenderBlood(guiGraphics, player, y, x)
            return false
        }

        if (AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM) >= 2) {
            RenderUtils.innerRenderSouls(guiGraphics, player, y, x)
            return false
        }

        return true
    }


}