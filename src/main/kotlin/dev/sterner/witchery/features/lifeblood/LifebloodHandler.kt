package dev.sterner.witchery.features.lifeblood

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player


object LifebloodHandler {

    /**
     * Ticks lifeblood regeneration for a player
     */
    fun tick(player: Player) {
        if (player.level().isClientSide) return

        val data = LifebloodPlayerAttachment.getData(player)

        if (data.canRegenerate()) {
            val currentTick = player.level().gameTime

            if (currentTick - data.lastRegenTick >= LifebloodPlayerAttachment.Data.REGEN_INTERVAL_TICKS) {
                val newData = data.copy(
                    lifebloodPoints = (data.lifebloodPoints + 1).coerceAtMost(data.getMaxRegenPoints()),
                    lastRegenTick = currentTick
                )
                LifebloodPlayerAttachment.setData(player, newData)
            }
        }
    }

    /**
     * Handles damage absorption by lifeblood
     * Returns the remaining damage after lifeblood absorption
     *
     * 1 damage = 1 lifeblood point (simple 1:1 ratio)
     */
    fun handleDamage(player: Player, source: DamageSource, damage: Float): Float {
        val data = LifebloodPlayerAttachment.getData(player)

        if (data.lifebloodPoints <= 0) {
            return damage
        }

        val damageInPoints = damage.toInt().coerceAtLeast(1)

        return if (damageInPoints >= data.lifebloodPoints) {
            val remainingDamage = damage - data.lifebloodPoints.toFloat()

            val newData = data.copy(
                lifebloodPoints = 0,
                lastRegenTick = 0
            )
            LifebloodPlayerAttachment.setData(player, newData)

            remainingDamage
        } else {
            val newData = data.copy(
                lifebloodPoints = data.lifebloodPoints - damageInPoints,
                lastRegenTick = player.level().gameTime
            )
            LifebloodPlayerAttachment.setData(player, newData)

            0f
        }
    }

    /**
     * Adds lifeblood points to a player
     */
    fun addLifeblood(player: Player, points: Int) {
        val data = LifebloodPlayerAttachment.getData(player)
        val newData = data.addPoints(points).copy(
            lastRegenTick = player.level().gameTime
        )
        LifebloodPlayerAttachment.setData(player, newData)
    }
}