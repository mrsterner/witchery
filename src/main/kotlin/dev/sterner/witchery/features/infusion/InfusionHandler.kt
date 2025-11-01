package dev.sterner.witchery.features.infusion

import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

object InfusionHandler {



    @JvmStatic
    fun increaseInfusionCharge(player: Player, toAdd: Int) {
        val currentCharge = InfusionPlayerAttachment.getInfusionCharge(player)
        val newCharge = (currentCharge + toAdd).coerceAtMost(InfusionPlayerAttachment.MAX_CHARGE)
        InfusionPlayerAttachment.setInfusionCharge(player, newCharge)
    }

    @JvmStatic
    fun decreaseInfusionCharge(player: Player, toRemove: Int) {
        val currentCharge = InfusionPlayerAttachment.getInfusionCharge(player)
        if (currentCharge > 0) {
            InfusionPlayerAttachment.setInfusionCharge(player, currentCharge - toRemove)
        }
    }

    /**
     * Checks if the player is holding the Witches' Hand in their main hand.
     *
     * @param player the player to check.
     * @return true if the player is holding the Witches' Hand, false otherwise.
     */
    private fun hasWitchesHand(player: Player): Boolean {
        return player.mainHandItem.`is`(WitcheryItems.WITCHES_HAND.get())
    }

    /**
     * Determines if the player can use infusion-related actions.
     * A player can use these actions if they are holding the Witches' Hand and have an active infusion type.
     *
     * @param player the player to check.
     * @return true if the player can use infusion actions, false otherwise.
     */
    fun canUse(player: Player): Boolean {
        return hasWitchesHand(player) && InfusionPlayerAttachment.getData(player).type != InfusionType.NONE
    }

    /**
     * Executes the infusion type's behavior for holding the right-click button.
     *
     * @param player the player performing the action.
     */
    fun onHoldRightClick(player: Player) {
        if (canUse(player)) {
            val infusionType = InfusionPlayerAttachment.getData(player).type
            infusionType.onHoldRightClick(player)
        }
    }

    /**
     * Executes the infusion type's behavior when releasing the right-click button.
     * If the player is sneaking, a separate behavior for shift-right-click is executed.
     *
     * @param player the player performing the action.
     */
    fun onHoldReleaseRightClick(player: Player) {
        if (canUse(player)) {
            val infusionType = InfusionPlayerAttachment.getData(player).type
            if (player.isShiftKeyDown) {
                infusionType.onReleaseRightClickShift(player)
            } else {
                infusionType.onReleaseRightClick(player)
            }
        }
    }


    fun leftClickEntity(
        player: Player,
        entity: Entity?,
    ) {
        if (canUse(player)) {
            val infusionType = InfusionPlayerAttachment.getData(player).type
            if (player.isShiftKeyDown) {
                infusionType.leftClickEntityShift(player, entity)
            } else {
                infusionType.leftClickEntity(player, entity)
            }
        }
    }

    /**
     * Handles the player's left-click action on a block while considering infusion-specific behavior.
     * Different actions are executed if the player is sneaking.
     *
     * @param player the player performing the action.
     * @param interactionHand the hand used for the interaction.
     * @param blockPos the position of the block being clicked.
     * @param direction the face of the block being clicked.
     * @return an {@link EventResult} indicating the result of the action.
     */
    fun leftClickBlock(
        player: Player,
        interactionHand: InteractionHand?,
        blockPos: BlockPos?,
    ) {
        if (canUse(player)) {
            val infusionType = InfusionPlayerAttachment.getData(player).type
            if (player.isShiftKeyDown) {
                infusionType.leftClickBlockShift(player, blockPos)
            } else {
                infusionType.leftClickBlock(player, blockPos)
            }
        }
    }

}