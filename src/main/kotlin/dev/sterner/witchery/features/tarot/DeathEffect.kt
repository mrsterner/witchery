package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import kotlin.math.cos
import kotlin.math.sin

class DeathEffect : TarotEffect(14) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.death.reversed" else "tarot.witchery.death"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.death.reversed.description" else "tarot.witchery.death.description"
    )

    override fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {
        if (!isReversed && entity.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE)) {
            if (player.level().random.nextFloat() < 0.1f && player.level() is ServerLevel) {
                val level = player.level() as ServerLevel

                val ethereal = entity.type.create(level)
                ethereal?.let { newEntity ->
                    if (newEntity is LivingEntity) {
                        newEntity.moveTo(entity.x, entity.y, entity.z, entity.yRot, entity.xRot)

                        EtherealEntityAttachment.setData(
                            newEntity,
                            EtherealEntityAttachment.Data(
                                ownerUUID = player.uuid,
                                canDropLoot = false,
                                isEthereal = true,
                                summonTime = level.gameTime,
                                maxLifeTime = 24000L
                            )
                        )

                        if (newEntity is Mob) {
                            newEntity.target = null
                            newEntity.setLastHurtByMob(null)
                        }

                        level.addFreshEntity(newEntity)

                        level.sendParticles(
                            ParticleTypes.SOUL,
                            entity.x, entity.y + entity.bbHeight / 2, entity.z,
                            20, 0.3, 0.3, 0.3, 0.05
                        )

                        level.playSound(
                            null, entity.blockPosition(),
                            SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE,
                            1.0f, 0.8f
                        )
                    }
                }
            }
        }
    }

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed) {
            if (player.level().gameTime % 200 == 0L) {
                player.causeFoodExhaustion(0.5f)
            }
        }
    }

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            val negativeEffects = player.activeEffects
                .filter { !it.effect.value().isBeneficial }
                .map { it.effect }

            negativeEffects.forEach { player.removeEffect(it) }
        }
    }

    override fun onNightfall(player: Player, isReversed: Boolean) {
        if (isReversed && player.level() is ServerLevel) {

            if (player.level().random.nextFloat() < 0.15f) {
                val level = player.level() as ServerLevel

                val deathEntity = WitcheryEntityTypes.DEATH.get().create(level)
                deathEntity?.let {
                    var spawnX: Double
                    var spawnZ: Double
                    var attempts = 0

                    do {
                        val angle = level.random.nextDouble() * 2 * Math.PI
                        val distance = 8.0 + level.random.nextDouble() * 12.0

                        spawnX = player.x + cos(angle) * distance
                        spawnZ = player.z + sin(angle) * distance

                        attempts++
                    } while (attempts < 10 && player.distanceToSqr(spawnX, player.y, spawnZ) < 64.0)

                    it.moveTo(spawnX, player.y, spawnZ, 0f, 0f)

                    it.setForcedTarget(player)

                    level.addFreshEntity(it)

                    level.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        spawnX, player.y + 1, spawnZ,
                        30, 0.5, 0.5, 0.5, 0.1
                    )

                    level.sendParticles(
                        ParticleTypes.LARGE_SMOKE,
                        spawnX, player.y, spawnZ,
                        20, 0.5, 0.5, 0.5, 0.05
                    )

                    level.playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.WITHER_SPAWN,
                        SoundSource.HOSTILE,
                        1.0f,
                        0.5f
                    )

                    player.displayClientMessage(
                        Component.literal("As night falls, Death comes for you!")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                        false
                    )
                    removeCardFromReading(player, this.cardNumber)
                }
            }
        }
    }
}