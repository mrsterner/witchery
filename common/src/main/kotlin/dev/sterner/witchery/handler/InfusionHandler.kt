package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.PlayerInfusionDataAttachment
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

object InfusionHandler {

    fun hasWitchesHand(player: Player): Boolean {
        return player.mainHandItem.`is`(WitcheryItems.WITCHES_HAND.get())
    }

    fun canUse(player: Player): Boolean {
        return hasWitchesHand(player) && PlayerInfusionDataAttachment.getPlayerInfusion(player).type != InfusionType.NONE
    }

    fun onHoldReleaseRightClick(player: Player, secondsHeld: Int) {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            val cost = if (player.isShiftKeyDown) {
                infusionType.onReleaseRightClickShift(player, secondsHeld)
            } else {
                infusionType.onReleaseRightClick(player, secondsHeld)
            }
            PlayerInfusionDataAttachment.decreaseInfusionCharge(player, cost)
        }
    }

    fun leftClickEntity(player: Player, level: Level?, entity: Entity?, interactionHand: InteractionHand?, entityHitResult: EntityHitResult?): EventResult? {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            val cost = if (player.isShiftKeyDown) {
                infusionType.leftClickEntityShift(player, entity, entityHitResult)
            } else {
                infusionType.leftClickEntity(player, entity, entityHitResult)
            }
            PlayerInfusionDataAttachment.decreaseInfusionCharge(player, cost)

        }
        return EventResult.pass()
    }

    fun leftClickBlock(player: Player, interactionHand: InteractionHand?, blockPos: BlockPos?, direction: Direction?): EventResult? {
        if (canUse(player)) {
            val infusionType = PlayerInfusionDataAttachment.getPlayerInfusion(player).type
            val cost = if (player.isShiftKeyDown) {
                infusionType.leftClickBlockShift(player, blockPos, direction)
            } else {
                infusionType.leftClickBlock(player, blockPos, direction)
            }
            PlayerInfusionDataAttachment.decreaseInfusionCharge(player, cost)
        }
        return EventResult.pass()
    }

}