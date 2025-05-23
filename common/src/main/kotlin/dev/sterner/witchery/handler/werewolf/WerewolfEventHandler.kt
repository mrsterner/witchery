package dev.sterner.witchery.handler.werewolf

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.HornedHuntsmanEntity
import dev.sterner.witchery.entity.WerewolfEntity
import dev.sterner.witchery.handler.ability.WerewolfAbility
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.payload.WerewolfAbilityUseC2SPayload
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.player.Player

object WerewolfEventHandler {

    private val overlay = Witchery.id("textures/gui/ability_hotbar_selection.png")

    fun handleHurtWolfman(entity: LivingEntity, damageSource: DamageSource, remainingDamage: Float): Float {
        return remainingDamage
    }

    fun handleHurtWolf(entity: LivingEntity, damageSource: DamageSource, remainingDamage: Float): Float {
        return remainingDamage
    }

    fun registerEvents() {
        EntityEvent.LIVING_DEATH.register(WerewolfEventHandler::killWerewolf)
        EntityEvent.LIVING_DEATH.register(WerewolfEventHandler::killSheep)
        EntityEvent.LIVING_DEATH.register(WerewolfEventHandler::killWolf)
        EntityEvent.LIVING_DEATH.register(WerewolfEventHandler::killHuntsman)
        EntityEvent.LIVING_DEATH.register(WerewolfEventHandler::killPiglin)
        EntityEvent.LIVING_DEATH.register(WerewolfEventHandler::killAny)
        TickEvent.PLAYER_PRE.register(WerewolfEventHandler::tick)
        InteractionEvent.RIGHT_CLICK_BLOCK.register(WerewolfEventHandler::rightClickBlockAbility)
    }

    private fun rightClickBlockAbility(
        player: Player?,
        interactionHand: InteractionHand?,
        blockPos: BlockPos?,
        direction: Direction?
    ): EventResult? {
        if (player == null || interactionHand == InteractionHand.OFF_HAND) {
            return EventResult.pass()
        }

        val playerData = WerewolfPlayerAttachment.getData(player)
        if (parseAbilityFromIndex(player, playerData.abilityIndex)) {
            return EventResult.interruptTrue()
        }

        return EventResult.pass()
    }

    fun clientRightClickAbility(player: Player?, interactionHand: InteractionHand?): Boolean {
        if (player == null || interactionHand == InteractionHand.OFF_HAND) {
            return false
        }

        val playerData = WerewolfPlayerAttachment.getData(player)
        if (playerData.abilityIndex != -1) {
            parseAbilityFromIndex(player, playerData.abilityIndex)
            NetworkManager.sendToServer(WerewolfAbilityUseC2SPayload(playerData.abilityIndex))
            return true
        }

        return false
    }

    fun infectPlayer(player: ServerPlayer) {
        if (WerewolfPlayerAttachment.getData(player).getWerewolfLevel() == 0) {
            WerewolfLeveling.increaseWerewolfLevel(player)
        }
    }

    private fun killWerewolf(werewolfEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (werewolfEntity is WerewolfEntity && damageSource?.entity is Player) {

        }

        return EventResult.pass()
    }

    private fun killAny(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        return EventResult.pass()
    }

    private fun killHuntsman(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is HornedHuntsmanEntity && damageSource?.entity is ServerPlayer) {
            val player = damageSource.entity as ServerPlayer
            WerewolfLeveling.setHasKilledHuntsman(player)
        }
        return EventResult.pass()
    }

    private fun killPiglin(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Piglin && damageSource?.entity is ServerPlayer) {
            val player = damageSource.entity as ServerPlayer
            WerewolfLeveling.increaseKilledPiglin(player)
        }
        return EventResult.pass()
    }

    private fun killWolf(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Wolf && damageSource?.entity is ServerPlayer) {
            val player = damageSource.entity as ServerPlayer
            WerewolfLeveling.increaseKilledWolf(player)
        }
        return EventResult.pass()
    }

    private fun killSheep(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Sheep && damageSource?.entity is ServerPlayer) {
            val player = damageSource.entity as ServerPlayer
            WerewolfLeveling.increaseKilledSheep(player)
        }
        return EventResult.pass()
    }

    private fun tick(player: Player) {
        if (!player.level().isClientSide) {
            if (player.level().gameTime % 20 == 0L) {
                val wereData = WerewolfPlayerAttachment.getData(player)

                if (!player.level().isDay && player.level().moonPhase == 0) {
                    if (wereData.getWerewolfLevel() > 0) {
                        val type = TransformationPlayerAttachment.getData(player).transformationType
                        if (type == TransformationPlayerAttachment.TransformationType.NONE) {
                            tryForceTurnToWerewolf(player, wereData)
                        }
                    }

                } else if (player.level().isDay || player.level().moonPhase != 0) {
                    if (wereData.getWerewolfLevel() > 0) {
                        tryForceTurnWerewolfToHuman(player, wereData)
                    }
                }
            }

            if (TransformationHandler.isWolf(player)) {
                wolfTick(player)
            } else if (TransformationHandler.isWerewolf(player)) {
                werewolfTick(player)
            }
        }
    }

    private fun werewolfTick(player: Player) {
        TransformationPlayerAttachment.sync(player, TransformationPlayerAttachment.getData(player))
    }

    private fun wolfTick(player: Player) {
        TransformationPlayerAttachment.sync(player, TransformationPlayerAttachment.getData(player))
    }

    private fun tryForceTurnWerewolfToHuman(player: Player, data: WerewolfPlayerAttachment.Data) {
        if (data.getWerewolfLevel() < 3) {
            TransformationHandler.removeForm(player)
        } else if (data.getWerewolfLevel() < 5) {
            TransformationHandler.removeForm(player)
        }
    }

    private fun tryForceTurnToWerewolf(player: Player, data: WerewolfPlayerAttachment.Data) {
        if (data.getWerewolfLevel() < 3) {
            TransformationHandler.setWolfForm(player)
        } else if (data.getWerewolfLevel() < 5) {
            TransformationHandler.setWereWolfForm(player)
        }
    }

    fun parseAbilityFromIndex(player: Player, abilityIndex: Int): Boolean {
        if (abilityIndex == WerewolfAbility.FREE_WOLF_TRANSFORM.ordinal) {
            val isWolf = TransformationHandler.isWolf(player)
            if (isWolf) {
                TransformationHandler.removeForm(player)
            } else {
                TransformationHandler.setWolfForm(player)
            }
            return true
        }
        if (abilityIndex == WerewolfAbility.FREE_WEREWOLF_TRANSITION.ordinal) {
            val isWerewolf = TransformationHandler.isWerewolf(player)
            if (isWerewolf) {
                TransformationHandler.removeForm(player)
            } else {
                TransformationHandler.setWereWolfForm(player)
            }
            return true
        }
        return false
    }

    fun renderHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val isNotWere = WerewolfPlayerAttachment.getData(player).getWerewolfLevel() <= 0

        if (isNotWere) {
            return
        }

        val abilityIndex = WerewolfPlayerAttachment.getData(player).abilityIndex
        val size = WerewolfAbilityHandler.getAbilities(player)

        val hasOffhand = !player.offhandItem.isEmpty

        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5 - if (hasOffhand) 32 else 0

        val bl2 = client.gameMode!!.canHurtPlayer()
        if (!bl2) {
            return
        }

        for (i in size.indices) {
            val name = size[i].id

            val iconX = x - (25 * i) + 4
            val iconY = y + 4

            guiGraphics.blit(
                Witchery.id("textures/gui/werewolf_abilities/${name}.png"),
                iconX, iconY,
                16, 16,
                0f, 0f, 16, 16,
                16, 16
            )
        }

        if (abilityIndex != -1) {
            guiGraphics.blit(overlay, x - (25 * abilityIndex), y, 24, 23, 0f, 0f, 24, 23, 24, 23)
        }
    }
}