package dev.sterner.witchery.mixin_logic


import dev.sterner.witchery.core.util.RenderUtils
import dev.sterner.witchery.core.util.RenderUtils.innerRenderBlood
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
object GuiMixinLogic {

    fun renderFoodLevel(player: Player, guiGraphics: GuiGraphics, y: Int, x: Int): Boolean {
        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0) {
            innerRenderBlood(guiGraphics, player, y, x)
            return false
        }

        if (AfflictionPlayerAttachment.getData(player).getLichLevel() >= 2) {
            RenderUtils.innerRenderSouls(guiGraphics, player, y, x)
            return false
        }

        return true
    }


}