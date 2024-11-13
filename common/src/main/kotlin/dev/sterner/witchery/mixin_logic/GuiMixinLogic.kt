package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.Witchery.id
import dev.sterner.witchery.api.RenderUtils.blitWithAlpha
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.Unique

object GuiMixinLogic {

    @JvmStatic
    fun `witchery$innerRenderBlood`(guiGraphics: GuiGraphics, player: Player, y: Int, x: Int) {
        val data = BloodPoolLivingEntityAttachment.getData(player)
        val bloodPool = data.bloodPool
        val maxBlood = data.maxBlood
        val dropCount = maxBlood / 300
        val fullIcons = bloodPool / 300
        val partialFill = bloodPool % 300
        val iconSize = 10
        for (i in 0 until dropCount) {
            val xPos = x - i * 7 - 8

            blitWithAlpha(
                guiGraphics.pose(),
                id("textures/gui/blood_pool_empty.png"),
                xPos,
                y - 1,
                0f,
                0f,
                iconSize,
                iconSize,
                iconSize,
                iconSize,
                1.0f,
                0xFFFFFF
            )

            if (i < fullIcons) {
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/blood_pool_full.png"),
                    xPos,
                    y - 1,
                    0f,
                    0f,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize,
                    1.0f,
                    0xFFFFFF
                )
            } else if (i == fullIcons && partialFill > 0) {
                val filledHeight = (partialFill * iconSize) / 300
                val emptyHeight = iconSize - filledHeight
                blitWithAlpha(
                    guiGraphics.pose(),
                    id("textures/gui/blood_pool_full.png"),
                    xPos,
                    y + emptyHeight - 1,
                    0f,
                    emptyHeight.toFloat(),
                    iconSize,
                    filledHeight,
                    iconSize,
                    iconSize,
                    1.0f,
                    0xFFFFFF
                )
            }
        }
    }
}