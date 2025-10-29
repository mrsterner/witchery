package dev.sterner.witchery.features.death

import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent

object DeathEquipmentEventHandler {

    fun onEquipmentChange(event: LivingEquipmentChangeEvent) {
        val entity = event.entity

        if (entity !is Player) return
        if (entity.level().isClientSide) return

        val slot = event.slot
        if (slot.isArmor || slot.name == "mainhand") {
            entity.level().server?.execute {
                checkAndUpdateDeathStatus(entity)
            }
        }
    }

    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity

        if (player.level().isClientSide) return
        if (player.tickCount % 20 != 0) return

        //checkAndUpdateDeathStatus(player) not needed?
    }

    private fun checkAndUpdateDeathStatus(player: Player) {
        val isCurrentlyDeath = DeathTransformationHelper.isPlayerFullyDeath(player)
        val wasDeath = DeathTransformationHelper.isDeath(player)

        DeathTransformationHelper.updateDeathStatus(player)

        if (isCurrentlyDeath && !wasDeath) {
            if (AfflictionAbilityHandler.abilityIndex != -1) {
                AfflictionAbilityHandler.setAbilityIndex(player, -1)
                player.inventory.selected = 0
            }
        } else if (!isCurrentlyDeath && wasDeath) {
            val data = DeathPlayerAttachment.getData(player)
            DeathPlayerAttachment.setData(
                player,
                data.copy(
                    hasDeathNightVision = false,
                    hasDeathFluidWalking = false
                )
            )
        }
    }
}