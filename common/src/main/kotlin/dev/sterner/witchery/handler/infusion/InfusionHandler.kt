package dev.sterner.witchery.handler.infusion

import dev.architectury.event.EventResult
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment.MAX_CHARGE
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment.getInfusionCharge
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment.setInfusionCharge
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.util.RenderUtils
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

/**
 * Utility class for handling actions related to player infusions and interactions.
 * Provides functionality to manage infusion-specific behaviors based on player actions such as right-clicking or left-clicking entities and blocks.
 */
object InfusionHandler {

    @JvmStatic
    fun increaseInfusionCharge(player: Player, toAdd: Int) {
        val currentCharge = getInfusionCharge(player)
        val newCharge = (currentCharge + toAdd).coerceAtMost(MAX_CHARGE)
        setInfusionCharge(player, newCharge)
    }

    @JvmStatic
    fun decreaseInfusionCharge(player: Player, toRemove: Int) {
        val currentCharge = getInfusionCharge(player)
        if (currentCharge > 0) {
            setInfusionCharge(player, currentCharge - toRemove)
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
    private fun canUse(player: Player): Boolean {
        return hasWitchesHand(player) && InfusionPlayerAttachment.getPlayerInfusion(player).type != InfusionType.NONE
    }

    /**
     * Executes the infusion type's behavior for holding the right-click button.
     *
     * @param player the player performing the action.
     */
    fun onHoldRightClick(player: Player) {
        if (canUse(player)) {
            val infusionType = InfusionPlayerAttachment.getPlayerInfusion(player).type
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
            val infusionType = InfusionPlayerAttachment.getPlayerInfusion(player).type
            if (player.isShiftKeyDown) {
                infusionType.onReleaseRightClickShift(player)
            } else {
                infusionType.onReleaseRightClick(player)
            }
        }
    }

    /**
     * Handles the player's left-click action on an entity while considering infusion-specific behavior.
     * Different actions are executed if the player is sneaking.
     *
     * @param player the player performing the action.
     * @param level the level in which the action is performed.
     * @param entity the entity being clicked.
     * @param interactionHand the hand used for the interaction.
     * @param entityHitResult the result of the entity hit.
     * @return an {@link EventResult} indicating the result of the action.
     */
    fun leftClickEntity(
        player: Player,
        level: Level?,
        entity: Entity?,
        interactionHand: InteractionHand?,
        entityHitResult: EntityHitResult?
    ): EventResult? {
        if (canUse(player)) {
            val infusionType = InfusionPlayerAttachment.getPlayerInfusion(player).type
            if (player.isShiftKeyDown) {
                infusionType.leftClickEntityShift(player, entity, entityHitResult)
            } else {
                infusionType.leftClickEntity(player, entity, entityHitResult)
            }
        }
        return EventResult.pass()
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
        direction: Direction?
    ): EventResult? {
        if (canUse(player)) {
            val infusionType = InfusionPlayerAttachment.getPlayerInfusion(player).type
            if (player.isShiftKeyDown) {
                infusionType.leftClickBlockShift(player, blockPos, direction)
            } else {
                infusionType.leftClickBlock(player, blockPos, direction)
            }
        }
        return EventResult.pass()
    }

    private val infusionMeter = Witchery.id("textures/gui/infusion_meter.png")
    private val infusionMeterOverlay = Witchery.id("textures/gui/infusion_meter_overlay.png")
    private val infusionMeterOtherwhere = Witchery.id("textures/gui/infusion_meter_otherwhere.png")
    private val infusionMeterInfernal = Witchery.id("textures/gui/infusion_meter_infernal.png")
    private val infusionMeterNecro = Witchery.id("textures/gui/infusion_meter_necro.png")
    private val infusionMeterOverworld = Witchery.id("textures/gui/infusion_meter_overworld.png")
    private val infusionMeterLight = Witchery.id("textures/gui/infusion_meter_light.png")

    @Environment(EnvType.CLIENT)
    fun renderInfusionHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker?) {
        val minecraft = Minecraft.getInstance()
        val clientPlayer = minecraft.player ?: return

        val data = InfusionPlayerAttachment.getPlayerInfusion(clientPlayer)
        if (data.type == InfusionType.NONE) return

        val scaledY = minecraft.window.guiScaledHeight
        val chargePercentage = data.charge.toFloat() / MAX_CHARGE

        val texture = when (data.type) {
            InfusionType.LIGHT -> {
                infusionMeterLight
            }

            InfusionType.OTHERWHERE -> {
                infusionMeterOtherwhere
            }

            InfusionType.INFERNAL -> {
                infusionMeterInfernal
            }

            InfusionType.NECRO -> {
                infusionMeterNecro
            }

            else -> infusionMeterOverworld
        }

        RenderUtils.blitWithAlpha(
            guiGraphics.pose(),
            infusionMeter,
            10,
            scaledY / 2 - (47 / 2),
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
            12 + 4,
            scaledY / 2 - (47 / 2) + (28 - otherwhereHeight) + 4,
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