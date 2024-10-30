package dev.sterner.witchery.handler

import com.mojang.authlib.minecraft.client.MinecraftClient
import dev.architectury.event.EventResult
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.PlayerInfusionDataAttachment
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import java.awt.Color

object InfusionHandler {

    fun hasWitchesHand(player: Player): Boolean {
        return player.mainHandItem.`is`(WitcheryItems.WITCHES_HAND.get())
    }

    fun canUse(player: Player): Boolean {
        return hasWitchesHand(player) && PlayerInfusionDataAttachment.getPlayerInfusion(player).type != InfusionType.NONE
    }

    fun onHoldRightClick(player: Player) {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            infusionType.onHoldRightClick(player)
        }
    }

    fun onHoldReleaseRightClick(player: Player) {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            if (player.isShiftKeyDown) {
                infusionType.onReleaseRightClickShift(player)
            } else {
                infusionType.onReleaseRightClick(player)
            }
        }
    }

    fun leftClickEntity(player: Player, level: Level?, entity: Entity?, interactionHand: InteractionHand?, entityHitResult: EntityHitResult?): EventResult? {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            if (player.isShiftKeyDown) {
                infusionType.leftClickEntityShift(player, entity, entityHitResult)
            } else {
                infusionType.leftClickEntity(player, entity, entityHitResult)
            }

        }
        return EventResult.pass()
    }

    fun leftClickBlock(player: Player, interactionHand: InteractionHand?, blockPos: BlockPos?, direction: Direction?): EventResult? {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            if (player.isShiftKeyDown) {
                infusionType.leftClickBlockShift(player, blockPos, direction)
            } else {
                infusionType.leftClickBlock(player, blockPos, direction)
            }
        }
        return EventResult.pass()
    }

    val infusionMeter = Witchery.id("textures/gui/infusion_meter.png")
    val infusionMeterOverlay = Witchery.id("textures/gui/infusion_meter_overlay.png")
    val infusionMeterOtherwhere = Witchery.id("textures/gui/infusion_meter_otherwhere.png")

    @Environment(EnvType.CLIENT)
    fun renderInfusionHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker?) {
        val minecraft = Minecraft.getInstance()
        val clientPlayer = minecraft.player ?: return

        val data = PlayerInfusionDataAttachment.getPlayerInfusion(clientPlayer)
        if (data.type == InfusionType.NONE) return

        val scaledY = minecraft.window.guiScaledHeight
        val chargePercentage = data.charge.toFloat() / InfusionData.MAX_CHARGE

        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            infusionMeter,
            10,
            scaledY - 100 + 30,
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
            infusionMeterOtherwhere,
            12 + 4,
            scaledY - 100 + (28 - otherwhereHeight) + 4 + 30,
            0f,
            (28 - otherwhereHeight).toFloat(),
            3,
            otherwhereHeight,
            3,
            28,
            1f
        )

        val overlayHeight = ((1f - chargePercentage) * 28).toInt()
        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            infusionMeterOverlay,
            12 + 4,
            scaledY - 100 + 4 + 30,
            0f,
            0f,
            3,
            overlayHeight,
            3,
            28,
            1f
        )
    }
}