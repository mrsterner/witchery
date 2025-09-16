package dev.sterner.witchery.handler.affliction

import com.mojang.blaze3d.platform.ScreenManager.clamp
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.affliction.TransformationHandler
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VampireClientSpecificEventHandler {

    private val sun = Witchery.id("textures/gui/affliction_abilities/sun_")

    /**
     * Renders the vampire HUD elements
     */
    @JvmStatic
    fun renderHud(guiGraphics: GuiGraphics) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val isNotVamp = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.VAMPIRISM) <= 0
        if (isNotVamp) return

        val canHurtPlayer = client.gameMode!!.canHurtPlayer()
        if (!canHurtPlayer) return

        val hasOffhand = !player.offhandItem.isEmpty
        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5 - if (hasOffhand) 32 else 0

        drawBloodSense(guiGraphics)
        drawSun(guiGraphics, player)
        drawBatFormHud(guiGraphics, player)
        AfflictionClientEventHandler.drawAbilityBar(guiGraphics, player, x, y)
    }


    /**
     * Draws the bat form HUD when player is transformed
     */
    private fun drawBatFormHud(guiGraphics: GuiGraphics, player: LocalPlayer) {
        if (TransformationHandler.isBat(player)) {
            val maxTicks = TransformationPlayerAttachment.getData(player).maxBatTimeClient
            val currentTicks = maxTicks - TransformationPlayerAttachment.getData(player).batFormTicker
            val hasArmor = player.armorValue > 0
            val y = guiGraphics.guiHeight() - 36 - 10 - if (hasArmor) 10 else 0
            val x = guiGraphics.guiWidth() / 2 - 18 * 4 - 11
            RenderUtils.innerRenderBat(guiGraphics, maxTicks, currentTicks, y, x)
        }
    }

    /**
     * Draws the sun-exposure HUD
     */
    private fun drawSun(guiGraphics: GuiGraphics, player: Player) {
        val y = guiGraphics.guiHeight() - 36 - 18 - 2
        val x = guiGraphics.guiWidth() / 2 - 8
        val raw = AfflictionPlayerAttachment.getData(player).getInSunTick()
        val maxSunTicks = AfflictionPlayerAttachment.getData(player).getMaxInSunTickClient()

        val sunTick = if (maxSunTicks > 0) {
            clamp((raw.toFloat() / maxSunTicks * 4).toInt(), 0, 4)
        } else {
            0
        }

        if (raw > 1) {
            RenderUtils.blitWithAlpha(
                guiGraphics.pose(),
                sun.withSuffix("${sunTick}.png"),
                x,
                y,
                0f,
                0f,
                16,
                16,
                16,
                16
            )
        }
    }

    /**
     * Draws the crosshair entity's blood pool indicator
     */
    private fun drawBloodSense(guiGraphics: GuiGraphics) {
        val x = guiGraphics.guiWidth() / 2 + 13
        val y = guiGraphics.guiHeight() / 2 + 9
        val target = Minecraft.getInstance().crosshairPickEntity

        if (target is LivingEntity && BloodPoolLivingEntityAttachment.getData(target).maxBlood > 0) {
            RenderUtils.innerRenderBlood(guiGraphics, target, y, x)
        }
    }
}