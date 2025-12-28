package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import net.minecraft.tags.BiomeTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.Tags

class CurseOfOverheating : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (player.level().isClientSide) return

        val curseData = CursePlayerAttachment.getData(player).playerCurseList
            .find { it.curseId == WitcheryCurseRegistry.CURSES_REGISTRY.getKey(this) }

        val witchPower = curseData?.witchPower ?: 0

        val biome = level.getBiome(player.blockPosition())

        val canSeeSun = player.level().canSeeSky(player.blockPosition()) && player.level().isDay
        val ultraWarm = player.level().dimensionType().ultraWarm()
        val isHot = biome.`is`(Tags.Biomes.IS_HOT)

        val witchPowerAmplifier = 1.0f + (witchPower * 0.01f).coerceAtMost(0.1f)
        val randomChance = level.random.nextFloat() < (0.25f * witchPowerAmplifier)

        if (randomChance) {
            if ((canSeeSun && isHot) || ultraWarm) {
                val baseTicks = if (WitcheryApi.isWitchy(player)) {
                    40
                } else {
                    20
                }

                val bonusTicks = (witchPower * 0.5f).toInt().coerceAtMost(5)
                val fireTicks = baseTicks + bonusTicks

                player.remainingFireTicks = fireTicks
            }
        }

        super.onTickCurse(level, player, catBoosted)
    }
}