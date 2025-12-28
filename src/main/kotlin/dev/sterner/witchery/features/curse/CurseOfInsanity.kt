package dev.sterner.witchery.features.curse

import dev.sterner.witchery.content.entity.InsanityEntity.Companion.DATA_MIMIC
import dev.sterner.witchery.content.entity.InsanityEntity.InsanityMobType
import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.features.coven.CovenHandler
import dev.sterner.witchery.mixin.EntityAccessor
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfInsanity : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (level.isClientSide) return

        val curseData = CursePlayerAttachment.getData(player).playerCurseList
            .find { it.curseId == WitcheryCurseRegistry.CURSES_REGISTRY.getKey(this) }

        val witchPower = curseData?.witchPower ?: 0
        val failedAttempts = curseData?.failedRemovalAttempts ?: 0

        val baseInterval = if (WitcheryApi.isWitchy(player)) {
            20 * 60
        } else {
            20 * 180
        }

        val powerReduction = witchPower * 1 * 20
        val attemptReduction = failedAttempts * 5 * 20
        val scaledInterval = (baseInterval - powerReduction - attemptReduction).coerceAtLeast(20 * 10)

        if (level.gameTime % (scaledInterval + (level.random.nextDouble() * 30).toInt()) == 0L) {
            val pos = findLocationForInsanityMob(player.blockPosition(), level)
            if (pos != null) {

                val insanityEntity = WitcheryEntityTypes.INSANITY.get().create(level)
                insanityEntity?.moveTo(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

                val mimicType: InsanityMobType = InsanityMobType.entries.toTypedArray().random()

                insanityEntity?.entityData?.set(DATA_MIMIC, mimicType.name.lowercase())

                if (mimicType == InsanityMobType.ENDERMAN) {
                    val accessor: EntityAccessor = insanityEntity as EntityAccessor
                    accessor.`witchery$setEyeHeight`(2.55f)
                    insanityEntity.playSound(SoundEvents.ENDERMAN_SCREAM, 0.7f, 1.0f)
                    insanityEntity.hasPlayedAggroSound = true
                }

                insanityEntity?.let { (level as ServerLevel).tryAddFreshEntityWithPassengers(it) }
            }
        }

        val covenSize = if (level is ServerLevel) {
            CovenHandler.getActiveCovenSize(player, player.blockPosition())
        } else {
            0
        }

        val hasCovenBoost = covenSize >= 3

        if (hasCovenBoost) {
            if (level.gameTime % (20 * 30) == 0L && level.random.nextFloat() < 0.4f) {
                playInsanitySounds(level, player)
            }
        } else {
            if (level.gameTime % (20 * 90) == 0L && level.random.nextFloat() < 0.3f) {
                playInsanitySounds(level, player)
            }
        }
    }

    private fun findLocationForInsanityMob(center: BlockPos, level: Level): BlockPos? {
        val random = level.random

        val radiusX = 16
        val radiusY = 8
        val radiusZ = 16

        val positions = mutableListOf<BlockPos>()

        for (dx in -radiusX..radiusX) {
            for (dy in -radiusY..radiusY) {
                for (dz in -radiusZ..radiusZ) {
                    val pos = center.offset(dx, dy, dz)

                    val floor = level.getBlockState(pos)
                    val space1 = level.getBlockState(pos.above())
                    val space2 = level.getBlockState(pos.above(2))
                    val space3 = level.getBlockState(pos.above(3))

                    if (
                        floor.isSolid &&
                        space1.isAir && space2.isAir && space3.isAir
                    ) {
                        positions.add(pos.above())
                    }
                }
            }
        }

        return if (positions.isNotEmpty()) positions[random.nextInt(positions.size)] else null
    }

    private fun playInsanitySounds(level: Level, player: Player) {
        if (!level.isClientSide) {
            val soundType = level.random.nextInt(6)
            val offsetX = (level.random.nextDouble() - 0.5) * 16
            val offsetZ = (level.random.nextDouble() - 0.5) * 16
            val soundPos = player.blockPosition().offset(offsetX.toInt(), 0, offsetZ.toInt())

            val soundEvent = when (soundType) {
                0 -> SoundEvents.CREEPER_PRIMED
                1 -> SoundEvents.ENDERMAN_SCREAM
                2 -> {
                    if (level.dimension() == Level.NETHER) {
                        SoundEvents.GHAST_SCREAM
                    } else {
                        SoundEvents.CREEPER_PRIMED
                    }
                }
                3 -> {
                    if (player.blockPosition().y <= 0) {
                        when (level.random.nextInt(4)) {
                            0 -> SoundEvents.WARDEN_AMBIENT
                            1 -> SoundEvents.WARDEN_AGITATED
                            2 -> SoundEvents.WARDEN_ROAR
                            else -> SoundEvents.WARDEN_EMERGE
                        }
                    } else {
                        SoundEvents.ENDERMAN_SCREAM
                    }
                }
                4 -> SoundEvents.AMBIENT_CAVE.value()
                else -> SoundEvents.ENDERMAN_STARE
            }

            level.playSound(
                null,
                soundPos,
                soundEvent,
                SoundSource.HOSTILE,
                0.8f,
                0.8f + level.random.nextFloat() * 0.4f
            )
        }
    }
}