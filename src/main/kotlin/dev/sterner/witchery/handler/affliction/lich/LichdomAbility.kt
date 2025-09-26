package dev.sterner.witchery.handler.affliction.lich

import dev.sterner.witchery.api.entity.PlayerShellEntity
import dev.sterner.witchery.data_attachment.EtherealEntityAttachment
import dev.sterner.witchery.data_attachment.possession.EntityAiToggle
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.entity.player_shell.SoulShellPlayerEntity
import dev.sterner.witchery.handler.NecroHandler
import dev.sterner.witchery.handler.affliction.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.affliction.AfflictionAbility
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.EntityTypeTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
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
    SOUL_FORM(6, 20 * 5) {
        override val id = "soul_form"
        override val requiresTarget = false

        override fun use(player: Player): Boolean {
            if (player !is ServerPlayer) return false

            val afflictionData = AfflictionPlayerAttachment.getData(player)
            val possessionComponent = PossessionComponentAttachment.get(player)
            val possessionData = PossessionComponentAttachment.getPossessionData(player)

            val isPossessing = possessionData.isPossessing()
            val host = possessionComponent.getHost()

            return when {
                isPossessing && host != null -> {
                    exitPossessionToSoulForm(player)
                    true
                }
                afflictionData.isSoulForm() || afflictionData.isVagrant() -> {
                    false
                }
                else -> {
                    activateSoulForm(player)
                    true
                }
            }
        }

        override fun use(player: Player, target: Entity): Boolean {
            if (player !is ServerPlayer) return false

            val afflictionData = AfflictionPlayerAttachment.getData(player)
            val possessionData = PossessionComponentAttachment.getPossessionData(player)

            if (possessionData.isPossessing()) {
                return false
            }

            if (!afflictionData.isSoulForm()) {
                return false
            }

            return when (target) {
                is SoulShellPlayerEntity -> {
                    if (afflictionData.isVagrant()) {
                        return false
                    }

                    if (target.getOriginalUUID().orElse(null) == player.uuid) {
                        returnToShell(player, target)
                        AbilityCooldownManager.startCooldown(player, this)
                        true
                    } else {
                        false
                    }
                }
                is Mob -> {
                    val result = attemptPossession(player, target)
                    if (result) {
                        AbilityCooldownManager.startCooldown(player, this)
                    }
                    result
                }
                else -> {
                    false
                }
            }
        }

        private fun activateSoulForm(player: ServerPlayer) {
            val shell = PlayerShellEntity.createShellFromPlayer(player)
            player.level().addFreshEntity(shell)

            AfflictionPlayerAttachment.batchUpdate(player) {
                withSoulForm(true).withVagrant(false)
            }

            SoulShellPlayerEntity.enableFlight(player)
            player.abilities.flying = true
            player.onUpdateAbilities()

            val random = player.random
            player.deltaMovement = player.deltaMovement.add(
                (random.nextDouble() - 0.5) * 0.05,
                0.1 + random.nextDouble() * 0.1,
                (random.nextDouble() - 0.5) * 0.05
            )
            player.hurtMarked = true

            playEffects(player, SoundEvents.SOUL_ESCAPE.value(), ParticleTypes.SOUL)
        }

        private fun attemptPossession(player: ServerPlayer, target: Mob): Boolean {
            val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)

            val canPossess = when {
                target.type.`is`(EntityTypeTags.UNDEAD) -> lichLevel >= 6
                target.type.`is`(EntityTypeTags.ILLAGER) -> lichLevel >= 8
                else -> false
            }

            if (!canPossess || target.health <= 0 || target.isRemoved) {
                return false
            }

            val possessionComponent = PossessionComponentAttachment.get(player)
            val success = possessionComponent.startPossessing(target)

            if (success) {
                AfflictionPlayerAttachment.batchUpdate(player) {
                    withSoulForm(false).withVagrant(true)
                }

                SoulShellPlayerEntity.disableFlight(player)
                player.onUpdateAbilities()

                playEffects(target, SoundEvents.ENDERMAN_TELEPORT, ParticleTypes.PORTAL)
            }

            return success
        }

        private fun exitPossessionToSoulForm(player: ServerPlayer) {
            val possessionComponent = PossessionComponentAttachment.get(player)
            val host = possessionComponent.getHost()

            if (host != null) {
                possessionComponent.stopPossessing(false)
                host.hurt(host.damageSources().magic(), host.maxHealth * 0.5f)

                EntityAiToggle.toggleAi(host, EntityAiToggle.POSSESSION_MECHANISM_ID, false, false)

                AfflictionPlayerAttachment.batchUpdate(player) {
                    withSoulForm(true).withVagrant(false)
                }

                SoulShellPlayerEntity.enableFlight(player)
                player.abilities.flying = true

                val random = player.random
                player.deltaMovement = player.deltaMovement.add(
                    (random.nextDouble() - 0.5) * 0.1,
                    0.2 + random.nextDouble() * 0.1,
                    (random.nextDouble() - 0.5) * 0.1
                )
                player.hurtMarked = true
                player.onUpdateAbilities()

                playEffects(player, SoundEvents.SCULK_SHRIEKER_SHRIEK, ParticleTypes.SOUL_FIRE_FLAME)
            }
        }


        private fun returnToShell(player: ServerPlayer, shell: SoulShellPlayerEntity) {
            SoulShellPlayerEntity.replaceWithPlayer(player, shell)

            player.teleportTo(shell.x, shell.y, shell.z)

            AfflictionPlayerAttachment.batchUpdate(player) {
                withSoulForm(false).withVagrant(false)
            }

            SoulShellPlayerEntity.disableFlight(player)
            player.abilities.flying = false
            player.onUpdateAbilities()

            shell.discard()

            playEffects(player, SoundEvents.SOUL_ESCAPE.value(), ParticleTypes.SOUL)
        }

        private fun playEffects(entity: Entity, sound: SoundEvent, particle: ParticleOptions) {
            entity.level().playSound(
                null,
                entity.x, entity.y, entity.z,
                sound,
                SoundSource.PLAYERS,
                1.0f, 0.5f
            )

            if (entity.level() is ServerLevel) {
                val serverLevel = entity.level() as ServerLevel
                serverLevel.sendParticles(
                    particle,
                    entity.x, entity.y + 1, entity.z,
                    20,
                    0.5, 0.5, 0.5,
                    0.1
                )
            }
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
    }
}