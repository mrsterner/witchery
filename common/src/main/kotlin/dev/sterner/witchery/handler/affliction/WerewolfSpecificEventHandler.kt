package dev.sterner.witchery.handler.affliction

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.handler.werewolf.WerewolfLeveling
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.player.Player

object WerewolfSpecificEventHandler {

    fun registerEvents() {
        EntityEvent.LIVING_DEATH.register(::killEntity)
        TickEvent.PLAYER_PRE.register(::tick)
    }

    private fun killEntity(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Piglin && damageSource?.entity is ServerPlayer) {
            val player = damageSource.entity as ServerPlayer
            WerewolfLeveling.increaseKilledPiglin(player)
        }
        return EventResult.pass()
    }

    @JvmStatic
    fun tick(player: Player) {
        if (player !is ServerPlayer) return

        if (!player.level().isClientSide) {

            if (player.level().gameTime % 20 == 0L) {
                val wereData = AfflictionPlayerAttachment.getData(player)

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


    private fun tryForceTurnWerewolfToHuman(player: Player, data: AfflictionPlayerAttachment.Data) {
        if (data.getWerewolfLevel() < 3) {
            TransformationHandler.removeForm(player)
        } else if (data.getWerewolfLevel() < 5) {
            TransformationHandler.removeForm(player)
        }
    }

    private fun tryForceTurnToWerewolf(player: Player, data: AfflictionPlayerAttachment.Data) {
        if (data.getWerewolfLevel() < 3) {
            TransformationHandler.setWolfForm(player)
        } else if (data.getWerewolfLevel() < 5) {
            TransformationHandler.setWereWolfForm(player)
        }
    }

    fun handleHurtWolfman(entity: LivingEntity, damageSource: DamageSource, remainingDamage: Float): Float {
        return remainingDamage
    }

    fun handleHurtWolf(entity: LivingEntity, damageSource: DamageSource, remainingDamage: Float): Float {
        return remainingDamage
    }

    fun infectPlayer(player: ServerPlayer) {
        if (AfflictionPlayerAttachment.getData(player).getWerewolfLevel() == 0) {
            WerewolfLeveling.increaseWerewolfLevel(player)
        }
    }
}