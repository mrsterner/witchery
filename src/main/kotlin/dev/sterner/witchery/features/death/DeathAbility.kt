package dev.sterner.witchery.features.death

import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.ability.AfflictionAbility
import dev.sterner.witchery.features.death.DeathTransformationHelper.hasDeathBoots
import net.minecraft.world.entity.player.Player

enum class DeathAbility(
    override val requiredLevel: Int = 0,
    override val cooldown: Int,
    override val affliction: AfflictionTypes = AfflictionTypes.DEATH
) : AfflictionAbility {

    NIGHT_VISION(0, 20 * 1) {
        override val id: String
            get() = "death_night_vision"

        override fun use(player: Player): Boolean {
            val hadNightVision = DeathPlayerAttachment.getData(player).hasDeathNightVision

            DeathPlayerAttachment.setData(
                player,
                DeathPlayerAttachment.getData(player).copy(hasDeathNightVision = !hadNightVision)
            )

            return true
        }

        override fun isAvailable(player: Player, level: Int): Boolean {
            return DeathTransformationHelper.isDeath(player)
        }
    },

    FLUID_WALKING(0, 20 * 1) {
        override val id: String
            get() = "death_fluid_walking"

        override fun use(player: Player): Boolean {
            val hadFluidWalking = DeathPlayerAttachment.getData(player).hasDeathFluidWalking

            DeathPlayerAttachment.setData(
                player,
                DeathPlayerAttachment.getData(player).copy(hasDeathFluidWalking = !hadFluidWalking)
            )

            return true
        }

        override fun isAvailable(player: Player, level: Int): Boolean {
            return hasDeathBoots(player)
        }
    };
}