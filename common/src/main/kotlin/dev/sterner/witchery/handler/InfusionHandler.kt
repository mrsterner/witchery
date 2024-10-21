package dev.sterner.witchery.handler

import com.mojang.authlib.minecraft.client.MinecraftClient
import dev.architectury.event.EventResult
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

    fun onHoldReleaseRightClick(player: Player, secondsHeld: Int) {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            if (player.isShiftKeyDown) {
                infusionType.onReleaseRightClickShift(player, secondsHeld)
            } else {
                infusionType.onReleaseRightClick(player, secondsHeld)
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

    //TODO make graphic
    @Environment(EnvType.CLIENT)
    fun renderInfusionHud(guiGraphics: GuiGraphics?, deltaTracker: DeltaTracker?) {
        val minecraft = Minecraft.getInstance()
        val clientPlayer = minecraft.player ?: return
        val font = minecraft.font

        val data = PlayerInfusionDataAttachment.getPlayerInfusion(clientPlayer)
        if (data.type == InfusionType.NONE) {
            return
        }
        val scaledX = minecraft.window.guiScaledWidth / 2
        val scaledY = minecraft.window.guiScaledHeight
        guiGraphics?.drawCenteredString(font, Component.literal("${data.charge} / ${InfusionData.MAX_CHARGE}"), scaledX,scaledY - 72, Color(255, 255, 255).rgb)
    }

}