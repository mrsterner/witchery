package dev.sterner.witchery.client.hud

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.util.RenderUtils
import dev.sterner.witchery.features.bark_belt.BarkBeltPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionType
import dev.sterner.witchery.features.misc.HudPlayerAttachment
import dev.sterner.witchery.features.spirit_world.ManifestationHandler.MAX_TIME
import dev.sterner.witchery.features.spirit_world.ManifestationPlayerAttachment
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object DraggableHuds {

    val infusionMeter = Witchery.id("textures/gui/infusion_meter.png")
    val infusionMeterOtherwhere = Witchery.id("textures/gui/infusion_meter_otherwhere.png")
    val infusionMeterInfernal = Witchery.id("textures/gui/infusion_meter_infernal.png")
    val infusionMeterNecro = Witchery.id("textures/gui/infusion_meter_necro.png")
    val infusionMeterOverworld = Witchery.id("textures/gui/infusion_meter_overworld.png")
    val infusionMeterLight = Witchery.id("textures/gui/infusion_meter_light.png")

    fun renderBarkHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker?) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val bl = client.gameMode!!.canHurtPlayer()
        if (!bl) {
            return
        }

        val positions = HudPlayerAttachment.getData(player).hudPositions
        val (x, y) = positions.getBarkBeltPos(
            guiGraphics.guiWidth(),
            guiGraphics.guiHeight()
        )

        val bark = BarkBeltPlayerAttachment.getData(player)
        if (bark.maxBark > 0) {
            for (i in 0 until bark.maxBark) {
                RenderUtils.blitWithAlpha(
                    guiGraphics.pose(),
                    Witchery.id("textures/gui/bark_empty.png"),
                    x + i * 8,
                    y,
                    0f,
                    0f,
                    8,
                    8,
                    8,
                    8
                )
            }
            for (i in 0 until bark.currentBark) {
                RenderUtils.blitWithAlpha(
                    guiGraphics.pose(),
                    Witchery.id("textures/gui/bark_full.png"),
                    x + i * 8,
                    y,
                    0f,
                    0f,
                    8,
                    8,
                    8,
                    8
                )
            }
        }
    }

    fun renderManifestHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker?) {
        val minecraft = Minecraft.getInstance()
        val clientPlayer = minecraft.player ?: return

        val data = ManifestationPlayerAttachment.getData(clientPlayer)
        if (data.manifestationTimer <= 0) return

        val positions = HudPlayerAttachment.getData(clientPlayer).hudPositions
        val scaledY = minecraft.window.guiScaledHeight
        val (x, y) = positions.getManifestationPos(scaledY)

        val chargePercentage = data.manifestationTimer.toFloat() / MAX_TIME

        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            Witchery.id("textures/gui/zzz_meter_overlay.png"),
            x,
            y,
            0f,
            0f,
            12,
            24,
            12,
            24,
            1f
        )

        val overlayHeight = ((1f - chargePercentage) * 24).toInt()
        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            Witchery.id("textures/gui/zzz_meter.png"),
            x,
            y,
            0f,
            0f,
            12,
            overlayHeight,
            12,
            24,
            1f
        )
    }

    fun renderInfusionHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker?) {
        val minecraft = Minecraft.getInstance()
        val clientPlayer = minecraft.player ?: return

        val data = InfusionPlayerAttachment.getData(clientPlayer)
        if (data.type == InfusionType.NONE) return

        val positions = HudPlayerAttachment.getData(clientPlayer).hudPositions
        val scaledY = minecraft.window.guiScaledHeight
        val (x, y) = positions.getInfusionPos(scaledY)

        val chargePercentage = data.charge.toFloat() / InfusionPlayerAttachment.MAX_CHARGE

        val texture = when (data.type) {
            InfusionType.LIGHT -> infusionMeterLight
            InfusionType.OTHERWHERE -> infusionMeterOtherwhere
            InfusionType.INFERNAL -> infusionMeterInfernal
            InfusionType.NECRO -> infusionMeterNecro
            else -> infusionMeterOverworld
        }

        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            infusionMeter,
            x,
            y,
            0f,
            0f,
            15,
            47,
            15,
            47,
            1f
        )

        val otherwhereHeight = (chargePercentage * 28).toInt()
        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            texture,
            x + 6,
            y + (28 - otherwhereHeight) + 4,
            0f,
            (28 - otherwhereHeight).toFloat(),
            3,
            otherwhereHeight,
            3,
            28,
            1f
        )
    }
}