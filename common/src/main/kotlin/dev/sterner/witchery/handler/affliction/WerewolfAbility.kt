package dev.sterner.witchery.handler.affliction

import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.affliction.TransformationHandler
import dev.sterner.witchery.mixin_logic.SummonedWolf
import dev.sterner.witchery.platform.PlatformUtils
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import kotlin.math.cos
import kotlin.math.sin

enum class WerewolfAbility(
    override val requiredLevel: Int,
    override val cooldown: Int,
    override val affliction: AfflictionTypes = AfflictionTypes.LYCANTHROPY
) : AfflictionAbility {
    BITE(1, 20 * 5) {
        override val id: String = "bite"

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            val lookVec = player.lookAngle
            val lungeStrength = 1.2

            player.deltaMovement = player.deltaMovement.add(
                lookVec.x * lungeStrength,
                0.2,
                lookVec.z * lungeStrength
            )
            player.hurtMarked = true

            val searchBox = player.boundingBox
                .expandTowards(lookVec.scale(3.0))
                .inflate(1.0, 0.5, 1.0)

            val targets = player.level().getEntitiesOfClass(
                LivingEntity::class.java,
                searchBox
            ) { entity ->
                entity != player &&
                        entity.isAlive &&
                        !entity.isSpectator &&
                        player.canAttack(entity)
            }

            val target = targets.minByOrNull { player.distanceToSqr(it) }

            if (target != null) {
                val damage = if (TransformationHandler.isWerewolf(player)) 8.0f else 6.0f
                target.hurt(player.damageSources().playerAttack(player), damage)

                player.heal(1.0f)

                target.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1))

                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.WOLF_HURT,
                    SoundSource.PLAYERS,
                    1.0f,
                    0.8f
                )

                (player.level() as ServerLevel).sendParticles(
                    ParticleTypes.DAMAGE_INDICATOR,
                    target.x,
                    target.y + target.eyeHeight,
                    target.z,
                    5,
                    0.2,
                    0.2,
                    0.2,
                    0.1
                )

                return true
            }

            player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.WOLF_GROWL,
                SoundSource.PLAYERS,
                0.8f,
                1.0f
            )

            return true
        }
    },
    PACK_SUMMON(8, 20 * 60) {
        override val id: String = "pack_summon"

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            val wolfCount = if (TransformationHandler.isWerewolf(player)) 3 else 2

            for (i in 0 until wolfCount) {
                val wolf = EntityType.WOLF.create(player.level()) ?: continue

                val angle = (i * 2 * Math.PI) / wolfCount
                val xOffset = cos(angle) * 2
                val zOffset = sin(angle) * 2

                wolf.moveTo(
                    player.x + xOffset,
                    player.y,
                    player.z + zOffset,
                    player.yRot,
                    0f
                )

                wolf.tame(player)
                wolf.ownerUUID = player.uuid

                (wolf as SummonedWolf).`witchery$setSummoned`(true)
                (wolf as SummonedWolf).`witchery$setSummonDuration`(20 * 30)

                wolf.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 120, 1))
                wolf.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 120, 0))
                wolf.addEffect(MobEffectInstance(MobEffects.REGENERATION, 20 * 120, 0))

                player.level().addFreshEntity(wolf)

                (player.level() as ServerLevel).sendParticles(
                    ParticleTypes.HEART,
                    wolf.x,
                    wolf.y + 1,
                    wolf.z,
                    5,
                    0.5,
                    0.5,
                    0.5,
                    0.0
                )
            }

            player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.WOLF_HOWL,
                SoundSource.PLAYERS,
                1.5f,
                1.2f
            )

            return true
        }
    },

    WOLF_FORM(2, 20 * 2) {
        override val id: String = "wolf_form"

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            if (!hasMoonCharm(player)) return false

            if (TransformationHandler.isWolf(player)) {
                TransformationHandler.removeForm(player)
                AbilityCooldownManager.startCooldown(player, this)
            } else {
                TransformationHandler.setWolfForm(player)

                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.WOLF_GROWL,
                    SoundSource.PLAYERS,
                    1.0f,
                    0.5f
                )

                if (player is ServerPlayer) {
                    (player.level() as ServerLevel).sendParticles(
                        ParticleTypes.SMOKE,
                        player.x,
                        player.y + 1,
                        player.z,
                        20,
                        0.5,
                        0.5,
                        0.5,
                        0.05
                    )
                }
            }

            return true
        }

        override fun isAvailable(level: Int): Boolean {
            return level >= requiredLevel
        }
    },

    WEREWOLF_FORM(3, 20 * 2) {
        override val id: String = "werewolf_form"

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            if (!hasMoonCharm(player)) return false

            if (TransformationHandler.isWerewolf(player)) {
                TransformationHandler.removeForm(player)
                AbilityCooldownManager.startCooldown(player, this)
            } else {
                TransformationHandler.setWereWolfForm(player)

                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.WOLF_GROWL,
                    SoundSource.PLAYERS,
                    1.0f,
                    0.2f
                )

                if (player is ServerPlayer) {
                    (player.level() as ServerLevel).sendParticles(
                        ParticleTypes.ANGRY_VILLAGER,
                        player.x,
                        player.y + 1,
                        player.z,
                        10,
                        0.5,
                        0.5,
                        0.5,
                        0.0
                    )
                }
            }

            return true
        }

        override fun isAvailable(level: Int): Boolean {
            return level >= requiredLevel
        }
    };
    companion object {
        @JvmStatic
        fun hasMoonCharm(player: Player): Boolean {
            return PlatformUtils.allEquippedAccessories(player).map { it.item }.contains(WitcheryItems.MOON_CHARM.get())
        }
    }
}