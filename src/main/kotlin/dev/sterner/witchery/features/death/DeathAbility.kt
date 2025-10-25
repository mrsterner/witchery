package dev.sterner.witchery.features.death

import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.ability.AbilityCooldownManager
import dev.sterner.witchery.features.affliction.ability.AfflictionAbility
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

enum class DeathAbility(
    override val requiredLevel: Int = 0,
    override val cooldown: Int,
    override val affliction: AfflictionTypes = AfflictionTypes.VAMPIRISM
) : AfflictionAbility {

    NIGHT_VISION(0, 20 * 5) {
        override val id: String
            get() = "death_night_vision"

        override fun use(player: Player): Boolean {
            val hadNightVision = DeathPlayerAttachment.getData(player).hasDeathNightVision

            DeathPlayerAttachment.setData(
                player,
                DeathPlayerAttachment.getData(player).copy(hasDeathNightVision = !hadNightVision)
            )

            if (hadNightVision) {
                AbilityCooldownManager.startCooldown(player, this)
            }
            return true
        }

        override fun isAvailable(player: Player, level: Int): Boolean {
            return DeathTransformationHelper.isDeath(player)
        }
    },

    FLUID_WALKING(0, 20 * 5) {
        override val id: String
            get() = "death_fluid_walking"

        override fun use(player: Player): Boolean {
            val hadFluidWalking = DeathPlayerAttachment.getData(player).hasDeathFluidWalking

            DeathPlayerAttachment.setData(
                player,
                DeathPlayerAttachment.getData(player).copy(hasDeathFluidWalking = !hadFluidWalking)
            )

            if (hadFluidWalking) {
                AbilityCooldownManager.startCooldown(player, this)
            }
            return true
        }

        override fun isAvailable(player: Player, level: Int): Boolean {
            return DeathTransformationHelper.isDeath(player)
        }
    };

    override fun use(player: Player, target: Entity): Boolean {
        return use(player)
    }

    override fun use(player: Player): Boolean {
        return false
    }

    override fun isAvailable(player: Player, level: Int): Boolean {
        return DeathTransformationHelper.isDeath(player)
    }
}