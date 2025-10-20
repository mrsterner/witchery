package dev.sterner.witchery.features.affliction.lich

import dev.sterner.witchery.core.api.InventorySlots
import dev.sterner.witchery.core.api.entity.PlayerShellEntity
import dev.sterner.witchery.content.block.ancient_tablet.AncientTabletBlock
import dev.sterner.witchery.content.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.content.entity.player_shell.SoulShellPlayerEntity
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment
import dev.sterner.witchery.features.necromancy.PhylacteryLevelDataAttachment
import dev.sterner.witchery.features.necromancy.SoulPoolPlayerAttachment
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.possession.EntityAiToggle
import dev.sterner.witchery.features.possession.PossessionComponentAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.EntityTypeTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.animal.SnowGolem
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.entity.monster.ZombieVillager
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

object LichdomSpecificEventHandler {

    private const val PHYLACTERY_SKYLIGHT_CHECK_RATE = 200
    private const val SOUL_SEPARATION_DURATION = 20 * 30


    @JvmStatic
    fun tick(player: Player?) {
        if (player !is ServerPlayer) return

        val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
        if (player.isAlive && lichLevel > 0) {
            lichTick(player, lichLevel)
        }
    }

    private fun lichTick(player: ServerPlayer, level: Int) {
        handlePhylacteryRequirements(player, level)
        applySoulFormEffects(player, level)
    }

    private fun handlePhylacteryRequirements(player: ServerPlayer, level: Int) {
        if (level < 9) return

        if (player.tickCount % PHYLACTERY_SKYLIGHT_CHECK_RATE == 0) {
            val data = AfflictionPlayerAttachment.getData(player)

            if (data.isPhylacteryBound()) {
                val phylacteryPos =
                    PhylacteryLevelDataAttachment.listPhylacteriesForPlayer(player.level() as ServerLevel, player.uuid)
                for (phy in phylacteryPos) {

                    val canSeeSky = player.level().canSeeSky(phy.pos)

                    if (!canSeeSky) {
                        player.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, PHYLACTERY_SKYLIGHT_CHECK_RATE, 1))
                        player.addEffect(
                            MobEffectInstance(
                                MobEffects.MOVEMENT_SLOWDOWN,
                                PHYLACTERY_SKYLIGHT_CHECK_RATE,
                                0
                            )
                        )
                    }
                }

            }
        }
    }

    private fun applySoulFormEffects(player: ServerPlayer, level: Int) {
        val data = AfflictionPlayerAttachment.getData(player)


    }


    @JvmStatic
    fun onBlockInteract(
        event: PlayerInteractEvent.RightClickBlock,
        player: Player,
        hand: InteractionHand,
        pos: BlockPos
    ) {
        if (player !is ServerPlayer) return

        val blockState = player.level().getBlockState(pos)

        if (blockState.block is AncientTabletBlock) {
            val blockEntity = player.level().getBlockEntity(pos)

            if (blockEntity is AncientTabletBlockEntity) {
                val tabletId = blockEntity.getTabletId()

                if (LichdomLeveling.readAncientTablet(player, tabletId)) {

                    player.level().playSound(
                        null,
                        pos,
                        SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.BLOCKS,
                        1.0f,
                        0.5f
                    )

                    for (i in 0..10) {
                        val level = player.level() as ServerLevel
                        level.sendParticles(
                            ParticleTypes.ENCHANT,
                            pos.x + 0.5,
                            pos.y + 1.0,
                            pos.z + 0.5,
                            1,
                            0.5, 0.5, 0.5,
                            0.1
                        )
                    }
                }
                event.isCanceled = true
                return
            }
        }
    }

    @JvmStatic
    fun onKillEntity(livingEntity: LivingEntity, damageSource: DamageSource) {
        val player = damageSource.entity as? ServerPlayer ?: return

        val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
        if (lichLevel == 0) return

        // Check for zombie minion kills (level 2->3)
        if (damageSource.entity is Zombie && isPlayerMinion(damageSource.entity as Zombie, player)) {
            if (livingEntity is Monster) {
                LichdomLeveling.recordZombieKill(player)
            }
        }

        // Check for golem kills (level 3->4)
        if (livingEntity is IronGolem || livingEntity is SnowGolem) {
            LichdomLeveling.increaseKilledGolems(player)
        }

        // Check for possessed kill (level 6->7)
        val data = AfflictionPlayerAttachment.getData(player)
        if (data.isSoulForm() && livingEntity is Villager) {
            LichdomLeveling.recordPossessedKill(player)
        }

        // Check for Wither kill (level 7->8)
        if (livingEntity is WitherBoss) {
            LichdomLeveling.recordWitherKill(player)
        }
    }

    @JvmStatic
    fun onDeath(event: LivingDeathEvent, livingEntity: LivingEntity, damageSource: DamageSource) {
        if (livingEntity !is ServerPlayer) return

        val lichLevel = AfflictionPlayerAttachment.getData(livingEntity).getLevel(AfflictionTypes.LICHDOM)
        if (lichLevel < 2) return

        val currentSouls = LichdomSoulPoolHandler.getCurrentSouls(livingEntity)

        if (currentSouls > 0) {
            LichdomSoulPoolHandler.decreaseSouls(livingEntity, 1)

            livingEntity.health = livingEntity.maxHealth * 0.5f
            livingEntity.removeAllEffects()

            livingEntity.level().playSound(
                null,
                livingEntity.x,
                livingEntity.y,
                livingEntity.z,
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0f,
                0.7f
            )

            LichdomLeveling.recordPhylacteryUse(livingEntity)

            LichdomLeveling.recordPhylacteryTripleDeath(livingEntity)

            event.isCanceled = true
            return
        }
    }

    @JvmStatic
    fun respawn(newPlayer: Player, oldPlayer: Player, alive: Boolean) {
        val lichLevel = AfflictionPlayerAttachment.getData(oldPlayer).getLevel(AfflictionTypes.LICHDOM)

        if (lichLevel > 0) {
            val soulData = SoulPoolPlayerAttachment.getData(oldPlayer)
            SoulPoolPlayerAttachment.setData(
                newPlayer,
                SoulPoolPlayerAttachment.Data(soulData.maxSouls, 50)
            )

            AfflictionPlayerAttachment.smartUpdate(newPlayer) {
                withSoulForm(false)
            }

            val currentSouls = LichdomSoulPoolHandler.getCurrentSouls(newPlayer as ServerPlayer)
            LichdomSoulPoolHandler.setMaxSouls(newPlayer, lichLevel)

            val pool = SoulPoolPlayerAttachment.getData(newPlayer)
            SoulPoolPlayerAttachment.setData(newPlayer, pool.copy(soulPool = currentSouls))

        }
    }

    private fun isPlayerMinion(entity: LivingEntity, player: ServerPlayer): Boolean {
        return EtherealEntityAttachment.getData(entity).ownerUUID == player.uuid
    }

    fun returnToShell(player: ServerPlayer, shell: SoulShellPlayerEntity) {
        if (shell.isRemoved || shell.getOriginalUUID().orElse(null) != player.uuid) {
            return
        }

        shell.mergeSoulWithShell(player)

        player.teleportTo(shell.x, shell.y, shell.z)

        InventorySlots.unlockAll(player)
        if (AfflictionAbilityHandler.abilityIndex != -1) {
            AfflictionAbilityHandler.setAbilityIndex(player, -1)
            player.inventory.selected = 0
        }

        playEffects(player, SoundEvents.SOUL_ESCAPE.value(), ParticleTypes.SOUL)
    }

    fun activateSoulForm(player: ServerPlayer) {
        val shell = PlayerShellEntity.createShellFromPlayer(player)
        player.level().addFreshEntity(shell)

        AfflictionPlayerAttachment.smartUpdate(player) {
            withSoulForm(true).withVagrant(false)
        }

        SoulShellPlayerEntity.enableFlight(player)
        player.abilities.flying = true

        InventorySlots.lockAll(player)

        val random = player.random
        player.deltaMovement = player.deltaMovement.add(
            (random.nextDouble() - 0.5) * 0.1,
            0.2 + random.nextDouble() * 0.1,
            (random.nextDouble() - 0.5) * 0.1
        )
        player.hurtMarked = true
        player.onUpdateAbilities()

        playEffects(player, SoundEvents.SOUL_ESCAPE.value(), ParticleTypes.SOUL)
    }

    fun attemptPossession(player: ServerPlayer, target: Mob): Boolean {
        val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)

        val canPossess = when {
            target is ZombieVillager -> lichLevel >= 6
            target.type.`is`(EntityTypeTags.UNDEAD) -> true
            target.type.`is`(EntityTypeTags.ILLAGER) -> lichLevel >= 8
            else -> false
        }

        if (!canPossess || target.health <= 0 || target.isRemoved) {
            return false
        }

        val possessionComponent = PossessionComponentAttachment.get(player)
        val success = possessionComponent.startPossessing(target)

        if (success) {
            AfflictionPlayerAttachment.smartUpdate(player) {
                withSoulForm(false).withVagrant(true)
            }

            SoulShellPlayerEntity.disableFlight(player)
            player.onUpdateAbilities()
            InventorySlots.unlockAll(player)
            playEffects(target, SoundEvents.ENDERMAN_TELEPORT, ParticleTypes.PORTAL)
        }

        return success
    }

    fun exitPossessionToSoulForm(player: ServerPlayer) {
        val possessionComponent = PossessionComponentAttachment.get(player)
        val host = possessionComponent.getHost()

        if (host != null) {
            possessionComponent.stopPossessing(false)
            host.hurt(host.damageSources().magic(), host.maxHealth * 0.5f)

            EntityAiToggle.toggleAi(host, EntityAiToggle.POSSESSION_MECHANISM_ID, false, false)

            AfflictionPlayerAttachment.smartUpdate(player) {
                withSoulForm(true).withVagrant(false)
            }

            InventorySlots.lockAll(player)
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
}