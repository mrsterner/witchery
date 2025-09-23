package dev.sterner.witchery.handler.affliction

import dev.sterner.witchery.api.entity.PlayerShellEntity
import dev.sterner.witchery.data_attachment.EtherealEntityAttachment
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.entity.player_shell.SoulShellPlayerEntity
import dev.sterner.witchery.handler.NecroHandler
import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

enum class LichdomAbility(
    override val requiredLevel: Int,
    override val cooldown: Int,
    override val affliction: AfflictionTypes = AfflictionTypes.LICHDOM
) : AfflictionAbility {

    SUMMON_UNDEAD(2, 20 * 30) {
        override val id: String = "summon_undead"

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
            val radius = 5 + lichLevel

            NecroHandler.summonNecroAroundPos(
                player.level() as ServerLevel,
                player,
                player.blockPosition(),
                radius
            )

            AbilityCooldownManager.startCooldown(player, this)
            return true
        }
    },
    SOUL_FORM(6, 20 * 5) {  // Level 6 ability, 2 minute cooldown
        override val id: String = "soul_form"
        override val requiresTarget = false

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            val afflictionData = AfflictionPlayerAttachment.getData(player)
            if (!afflictionData.isSoulForm()) {
                activateSoulForm(player)
            }
            return true
        }

        override fun use(player: Player, target: Entity): Boolean {
            if (player !is ServerPlayer) return false

            val afflictionData = AfflictionPlayerAttachment.getData(player)

            if (afflictionData.isSoulForm()) {
                if (target is SoulShellPlayerEntity) {
                    val shellUUID = target.getOriginalUUID().orElse(null)

                    if (shellUUID == player.uuid) {
                        target.mergeSoulWithShell(player)
                        AbilityCooldownManager.startCooldown(player, this)
                        return true
                    }
                }
                return false
            }
            return true
        }

        private fun activateSoulForm(player: ServerPlayer) {
            val shell = PlayerShellEntity.createShellFromPlayer(player)
            player.level().addFreshEntity(shell)

            player.inventory.clearContent()

            player.abilities.mayfly = true
            player.abilities.flying = true
            player.onUpdateAbilities()

            player.addEffect(MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false))
            player.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, false))

            AfflictionPlayerAttachment.batchUpdate(player) {
                withSoulForm(true)
            }

            player.level().playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundEvents.SOUL_ESCAPE,
                SoundSource.PLAYERS,
                1.0f,
                0.5f
            )

            val serverLevel = player.level() as ServerLevel
            serverLevel.sendParticles(
                ParticleTypes.SOUL,
                player.x,
                player.y + 1,
                player.z,
                20,
                0.5, 0.5, 0.5,
                0.1
            )
        }
    },
    CORPSE_EXPLOSION(3, 20 * 10) {
        override val id: String = "corpse_explosion"
        override val requiresTarget = false

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
            val explosionPower = 2.0f + (lichLevel * 0.5f)
            val range = 20.0 + (lichLevel * 2.0)

            val summonedUndead = player.level().getEntitiesOfClass(
                LivingEntity::class.java,
                player.boundingBox.inflate(range)
            ) { entity ->
                entity.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE) &&
                        EtherealEntityAttachment.getData(entity).ownerUUID == player.uuid
            }

            if (summonedUndead.isEmpty()) return false

            for (undead in summonedUndead) {

                undead.level().explode(
                    player,
                    undead.x,
                    undead.y + 0.5,
                    undead.z,
                    explosionPower,
                    Level.ExplosionInteraction.MOB
                )

                undead.hurt(undead.damageSources().magic(), Float.MAX_VALUE)
            }

            player.level().playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundEvents.CREEPER_PRIMED,
                SoundSource.PLAYERS,
                1.0f,
                0.5f
            )

            AbilityCooldownManager.startCooldown(player, this)
            return true
        }
    },

    LIFE_DRAIN(4, 20 * 5) {
        override val id: String = "life_drain"
        override val requiresTarget = true

        override fun use(player: Player, target: Entity): Boolean {
            if (player !is ServerPlayer || target !is LivingEntity) return false

            val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
            val drainAmount = 2.0f + (lichLevel * 0.5f)

            target.hurt(player.damageSources().magic(), drainAmount)

            player.heal(drainAmount * 0.5f)

            if (!target.isAlive) {
                val soulValue = when (target) {
                    is Villager -> 10
                    is Player -> 25
                    is Animal -> 1
                    else -> 2
                }
                LichdomSoulPoolHandler.increaseSouls(player, soulValue)
            }

            val level = player.level() as ServerLevel
            level.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                target.x,
                target.y + target.bbHeight / 2,
                target.z,
                10,
                0.3, 0.3, 0.3,
                0.1
            )

            AbilityCooldownManager.startCooldown(player, this)
            return true
        }
    },

    DEATH_TELEPORT(5, 20 * 60) {
        override val id: String = "death_teleport"

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            val lastDeathPos = player.lastDeathLocation.orElse(null) ?: return false

            if (lastDeathPos.dimension() == player.level().dimension()) {
                player.teleportTo(
                    lastDeathPos.pos().x + 0.5,
                    lastDeathPos.pos().y.toDouble(),
                    lastDeathPos.pos().z + 0.5
                )

                player.level().playSound(
                    null,
                    player.x,
                    player.y,
                    player.z,
                    SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS,
                    1.0f,
                    0.5f
                )

                AbilityCooldownManager.startCooldown(player, this)
                return true
            }

            return false
        }
    },
}