package dev.sterner.witchery.handler.werewolf

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.entity.WerewolfEntity
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.player.Player

object WerewolfEventHandler {
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
        PlayerEvent.PLAYER_CLONE.register(WerewolfEventHandler::respawn)
    }

    fun infectPlayer(player: Player) {

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
        //TODO add Huntsman
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
        if (player.level().gameTime % 20 == 0L) {
            val wereData = WerewolfPlayerAttachment.getData(player)

            if (!player.level().isDay && player.level().moonPhase == 0) {

                if (wereData.werewolfLevel > 0) {
                    val type = TransformationPlayerAttachment.getData(player).transformationType
                    if (type == TransformationPlayerAttachment.TransformationType.NONE) {
                        tryForceTurnToWerewolf(player, wereData)
                    }
                }
            } else {
                if (wereData.werewolfLevel > 0) {
                    tryForceTurnWerewolfToHuman(player, wereData)
                }
            }
        }
        
        if (TransformationPlayerAttachment.isWolf(player)) {
            wolfTick(player)
        } else if(TransformationPlayerAttachment.isWerewolf(player)) {
            werewolfTick(player)
        }
    }

    private fun werewolfTick(player: Player) {

    }

    private fun wolfTick(player: Player) {

    }

    private fun tryForceTurnWerewolfToHuman(player: Player, data: WerewolfPlayerAttachment.Data) {
        if (data.werewolfLevel < 3) {
            TransformationPlayerAttachment.removeForm(player)
        } else if (data.werewolfLevel < 5) {
            TransformationPlayerAttachment.removeForm(player)
        }
    }

    private fun tryForceTurnToWerewolf(player: Player, data: WerewolfPlayerAttachment.Data) {
        if (data.werewolfLevel < 3) {
            TransformationPlayerAttachment.setWolfForm(player)
        } else if (data.werewolfLevel < 5) {
            TransformationPlayerAttachment.setWereWolfForm(player)
        }
    }

    private fun respawn(serverPlayer: ServerPlayer?, serverPlayer1: ServerPlayer?, b: Boolean) {

    }


}