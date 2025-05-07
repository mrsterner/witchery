package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import dev.sterner.witchery.util.RenderUtils.innerRenderBlood
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.player.Player

object GuiMixinLogic {

    fun renderFoodLevel(player: Player, guiGraphics: GuiGraphics, y: Int, x: Int): Boolean {
        if (getData(player).getVampireLevel() > 0) {
            innerRenderBlood(guiGraphics, player, y, x)
            return false
        }

        return true
    }
}