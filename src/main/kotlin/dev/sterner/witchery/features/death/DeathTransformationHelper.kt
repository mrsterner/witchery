package dev.sterner.witchery.features.death

import dev.sterner.witchery.content.entity.DeathEntity
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.affliction.ability.AbilityScrollHandler
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler
import dev.sterner.witchery.features.misc.MiscPlayerAttachment
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object DeathTransformationHelper {

    private var death: DeathEntity? = null

    fun getDeathEntity(player: Player): DeathEntity? {
        if (death == null) {
            death = WitcheryEntityTypes.DEATH.get().create(player.level())
        }
        return this.death
    }

    fun isPlayerFullyDeath(player: Player): Boolean {
        val helmet = player.getItemBySlot(EquipmentSlot.HEAD)
        val chestplate = player.getItemBySlot(EquipmentSlot.CHEST)
        val boots = player.getItemBySlot(EquipmentSlot.FEET)

        val hasHelmet = helmet.`is`(WitcheryItems.DEATH_HOOD.get())
        val hasChestplate = chestplate.`is`(WitcheryItems.DEATH_ROBE.get())
        val hasBoots = boots.`is`(WitcheryItems.DEATH_BOOTS.get())

        return hasHelmet && hasChestplate && hasBoots
    }

    fun isDeath(player: Player): Boolean {
        return DeathPlayerAttachment.getData(player).isDeath
    }

    fun updateDeathStatus(player: Player) {
        val shouldBeDeath = isPlayerFullyDeath(player)
        val currentData = DeathPlayerAttachment.getData(player)

        if (currentData.isDeath != shouldBeDeath) {
            DeathPlayerAttachment.setData(player, currentData.copy(isDeath = shouldBeDeath))
        }
        if (!shouldBeDeath) {
            if (AfflictionAbilityHandler.abilityIndex != -1) {
                AfflictionAbilityHandler.setAbilityIndex(player, -1)
                player.inventory.selected = 0
            }
        }
    }

    fun findDeathPlayer(level: Level): Player? {
        for (player in level.players()) {
            val data = DeathPlayerAttachment.getData(player)
            if (data.isDeath && isPlayerFullyDeath(player)) {
                return player
            }
        }
        return null
    }

    fun hasDeathHood(player: Player): Boolean {
        return player.getItemBySlot(EquipmentSlot.HEAD).`is`(WitcheryItems.DEATH_HOOD.get())
    }

    fun hasDeathRobe(player: Player): Boolean {
        return player.getItemBySlot(EquipmentSlot.CHEST).`is`(WitcheryItems.DEATH_ROBE.get())
    }

    fun hasDeathBoots(player: Player): Boolean {
        return player.getItemBySlot(EquipmentSlot.FEET).`is`(WitcheryItems.DEATH_BOOTS.get())
    }
}