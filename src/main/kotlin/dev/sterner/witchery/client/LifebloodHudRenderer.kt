package dev.sterner.witchery.client

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.lifeblood.LifebloodPlayerAttachment
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import kotlin.math.ceil

object LifebloodHudRenderer {

    private val LIFEBLOOD_EMPTY = Witchery.id("textures/gui/lifeblood_empty.png")
    private val LIFEBLOOD_1 = Witchery.id("textures/gui/lifeblood_1.png")
    private val LIFEBLOOD_2 = Witchery.id("textures/gui/lifeblood_2.png")
    private val LIFEBLOOD_3 = Witchery.id("textures/gui/lifeblood_3.png")
    private val LIFEBLOOD_4 = Witchery.id("textures/gui/lifeblood_4.png")
    private val LIFEBLOOD_5 = Witchery.id("textures/gui/lifeblood_full.png")

    fun renderHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return

        if (minecraft.options.hideGui || player.isSpectator || player.isCreative) return

        val data = LifebloodPlayerAttachment.getData(player)
        if (data.lifebloodPoints <= 0) return

        val screenWidth = minecraft.window.guiScaledWidth
        val screenHeight = minecraft.window.guiScaledHeight

        val healthBarY = screenHeight - 39
        var lifebloodY = healthBarY - 11
        if (player.armorValue > 0) {
            lifebloodY += 10
        }
        if (player.absorptionAmount > 0) {
            lifebloodY += 10
        }

        renderLifebloodHearts(guiGraphics, player, data, screenWidth, lifebloodY)
    }

    private fun renderLifebloodHearts(
        guiGraphics: GuiGraphics,
        player: Player,
        data: LifebloodPlayerAttachment.Data,
        screenWidth: Int,
        y: Int
    ) {
        val poseStack = guiGraphics.pose()

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val totalHearts = ceil(data.lifebloodPoints / 5.0).toInt()
        val startX = screenWidth / 2 - 92

        for (i in 0 until totalHearts) {
            val heartIndex = i
            val x = startX + (heartIndex * 8)

            val pointsInThisHeart = if (heartIndex == totalHearts - 1) {
                val remainder = data.getPartialHeartPoints()
                if (remainder == 0) 5 else remainder
            } else {
                5
            }

            val texture = getLifebloodTexture(pointsInThisHeart)

            poseStack.pushPose()
            RenderSystem.setShaderTexture(0, texture)
            guiGraphics.blit(texture, x, y, 0f, 0f, 10, 10, 10, 10)
            poseStack.popPose()
        }

        RenderSystem.disableBlend()
    }

    private fun getLifebloodTexture(points: Int): ResourceLocation {
        return when (points) {
            0 -> LIFEBLOOD_EMPTY
            1 -> LIFEBLOOD_1
            2 -> LIFEBLOOD_2
            3 -> LIFEBLOOD_3
            4 -> LIFEBLOOD_4
            else -> LIFEBLOOD_5
        }
    }
}