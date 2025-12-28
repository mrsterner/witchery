package dev.sterner.witchery.core.api

import dev.sterner.witchery.features.coven.CovenHandler
import dev.sterner.witchery.features.familiar.FamiliarHandler
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player

object WitcheryPowerHelper {
    const val CAT_POWER = 7
    const val WITCH_POWER_PER_COVEN_MEMBER = 1
    const val MAX_COVEN_POWER = 6
    const val MAX_TOTAL_POWER = 14

    fun calculateWitchPower(
        player: Player
    ): Int {
        var power = 0

        if (player.level() is ServerLevel) {
            val hasCat = FamiliarHandler.getFamiliarEntityType(
                player.uuid,
                player.level() as ServerLevel
            ) == EntityType.CAT

            if (hasCat) {
                power += CAT_POWER
            }
        }

        if (player.level() is ServerLevel) {
            val covenSize = CovenHandler.getActiveCovenSize(
                player,
                player.blockPosition()
            )
            power += (covenSize * WITCH_POWER_PER_COVEN_MEMBER).coerceAtMost(MAX_COVEN_POWER)
        }

        return power.coerceIn(0, MAX_TOTAL_POWER)
    }

    fun canRemoveCurse(removerPower: Int, castingPower: Int, failedAttempts: Int): Boolean {
        val totalCursePower = castingPower + failedAttempts
        return removerPower >= totalCursePower
    }
}