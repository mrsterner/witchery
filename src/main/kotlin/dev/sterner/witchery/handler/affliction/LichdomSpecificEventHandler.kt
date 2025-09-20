package dev.sterner.witchery.handler.affliction

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlock
import dev.sterner.witchery.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.platform.EtherealEntityAttachment
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.PhylacteryLevelDataAttachment
import dev.sterner.witchery.platform.transformation.SoulPoolPlayerAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.animal.SnowGolem
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player

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
                val phylacteryPos = PhylacteryLevelDataAttachment.listPhylacteriesForPlayer(player.level() as ServerLevel, player.uuid)
                for (phy in phylacteryPos) {

                    val canSeeSky = player.level().canSeeSky(phy.pos)

                    if (!canSeeSky) {
                        player.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, PHYLACTERY_SKYLIGHT_CHECK_RATE, 1))
                        player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, PHYLACTERY_SKYLIGHT_CHECK_RATE, 0))
                    }
                }

            }
        }
    }

    private fun applySoulFormEffects(player: ServerPlayer, level: Int) {
        val data = AfflictionPlayerAttachment.getData(player)

        if (data.isSoulForm()) {
            player.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, 40))
            player.addEffect(MobEffectInstance(MobEffects.SLOW_FALLING, 40))
            player.isInvulnerable = true

            if (player.tickCount % SOUL_SEPARATION_DURATION == 0) {
                AfflictionPlayerAttachment.batchUpdate(player) {
                    withSoulForm(false)
                }
                player.isInvulnerable = false
            }
        }
    }


    @JvmStatic
    fun onBlockInteract(player: Player, hand: InteractionHand, pos: BlockPos, face: Direction): EventResult {
        if (player !is ServerPlayer) return EventResult.pass()

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

                return EventResult.interruptTrue()
            }
        }

        return EventResult.pass()
    }

    @JvmStatic
    fun onKillEntity(livingEntity: LivingEntity, damageSource: DamageSource): EventResult {
        val player = damageSource.entity as? ServerPlayer ?: return EventResult.pass()

        val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
        if (lichLevel == 0) return EventResult.pass()

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

        return EventResult.pass()
    }

    @JvmStatic
    fun onDeath(livingEntity: LivingEntity, damageSource: DamageSource): EventResult {
        if (livingEntity !is ServerPlayer) return EventResult.pass()

        val lichLevel = AfflictionPlayerAttachment.getData(livingEntity).getLevel(AfflictionTypes.LICHDOM)
        if (lichLevel < 2) return EventResult.pass()

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

            return EventResult.interruptFalse()
        }

        return EventResult.pass()
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

            AfflictionPlayerAttachment.batchUpdate(newPlayer) {
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
}