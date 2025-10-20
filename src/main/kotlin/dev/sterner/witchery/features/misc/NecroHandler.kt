package dev.sterner.witchery.features.misc

import dev.sterner.witchery.core.data_attachment.EtherealEntityAttachment
import dev.sterner.witchery.core.data_attachment.NecromancerLevelAttachment
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.network.SpawnNecroParticlesS2CPayload
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryTags
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionType
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor

object NecroHandler {

    private const val MINECRAFT_DAY = 24000
    private const val PARTICLE_SPAWN_RADIUS = 0.5
    private const val PARTICLE_DETECTION_RANGE = 16.0
    private const val PARTICLE_COUNT = 3


    fun tickLiving(livingEntity: LivingEntity) {
        if (livingEntity.level().gameTime % 10 != 0L) return

        if (livingEntity.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE)) {
            val etherealData = EtherealEntityAttachment.getData(livingEntity)
            if (etherealData.isEthereal) {
                EtherealEntityAttachment.sync(livingEntity, etherealData)

                if (etherealData.maxLifeTime > 0) {
                    val timeAlive = livingEntity.level().gameTime - etherealData.summonTime
                    if (timeAlive >= etherealData.maxLifeTime) {
                        discardNecro(livingEntity)
                    }
                }
            }
        }
    }

    fun discardNecro(livingEntity: LivingEntity) {
        if (livingEntity.level() is ServerLevel) {
            val level = livingEntity.level() as ServerLevel
            level.sendParticles(
                ParticleTypes.SOUL,
                livingEntity.x,
                livingEntity.y + livingEntity.bbHeight / 2,
                livingEntity.z,
                10,
                0.3, 0.3, 0.3,
                0.05
            )
        }
        livingEntity.discard()
    }

    fun tick(level: Level) {
        if (level.isClientSide) {
            return
        }

        val serverLevel = level as ServerLevel
        if (serverLevel.gameTime % 10 != 0L) return

        val necroData = NecromancerLevelAttachment.getData(serverLevel)
        if (necroData.necroList.isEmpty()) return

        val playersWithHand = serverLevel.players().filter { playerHasWitchHand(it) }
        if (playersWithHand.isEmpty()) return

        for (necroEntry in necroData.necroList) {
            val pos = necroEntry.pos ?: continue

            for (player in playersWithHand) {
                if (player !is ServerPlayer) continue

                val playerPos = player.position()
                val blockPos = Vec3.atCenterOf(pos)

                if (playerPos.distanceToSqr(blockPos) <= PARTICLE_DETECTION_RANGE * PARTICLE_DETECTION_RANGE) {
                    spawnNecroParticles(serverLevel, player, pos)
                }
            }
        }
    }

    private fun playerHasWitchHand(player: ServerPlayer): Boolean {
        val bl = player.mainHandItem.`is`(WitcheryItems.WITCHES_HAND.get()) ||
                player.offhandItem.`is`(WitcheryItems.WITCHES_HAND.get())
        val bl2 = InfusionPlayerAttachment.getData(player).type == InfusionType.NECRO
        return bl && bl2
    }

    private fun spawnNecroParticles(level: ServerLevel, player: ServerPlayer, pos: BlockPos) {
        val blockCenter = Vec3.atCenterOf(pos)

        for (i in 0 until PARTICLE_COUNT) {
            val offsetX = (level.random.nextDouble() - 0.5) * PARTICLE_SPAWN_RADIUS
            val offsetY = (level.random.nextDouble() - 0.5) * PARTICLE_SPAWN_RADIUS
            val offsetZ = (level.random.nextDouble() - 0.5) * PARTICLE_SPAWN_RADIUS

            val x = blockCenter.x + offsetX
            val y = blockCenter.y + offsetY
            val z = blockCenter.z + offsetZ

            PacketDistributor.sendToPlayersInDimension(level, SpawnNecroParticlesS2CPayload(Vec3(x, y, z)))
        }
    }

    fun processListExhaustion(level: Level) {
        if (level.isClientSide) return

        val serverLevel = level as ServerLevel

        val currentTime = serverLevel.gameTime
        if (currentTime % MINECRAFT_DAY != 0L) return

        val data = NecromancerLevelAttachment.getData(serverLevel)

        val filteredList = data.necroList.filter { entry ->
            val entryTime = entry.timestamp
            entryTime == null || currentTime - entryTime <= (MINECRAFT_DAY * 7)
        }

        if (filteredList.size != data.necroList.size) {
            val mutable = data.necroList.toMutableList()
            mutable.clear()
            mutable.addAll(filteredList)

            NecromancerLevelAttachment.setData(serverLevel, data.copy(necroList = mutable))
        }
    }

    fun addNecro(entity: LivingEntity) {
        val serverLevel = entity.level() as? ServerLevel ?: return
        val data = NecromancerLevelAttachment.getData(serverLevel)

        val exactPos = entity.blockPosition()
        val newData = NecromancerLevelAttachment.Necro(exactPos, entity.type, entity.level().gameTime)

        val newList = data.necroList.toMutableList()
        newList.add(newData)
        data.necroList = newList

        NecromancerLevelAttachment.setData(serverLevel, data)
    }

    fun onDeath(livingEntity: LivingEntity?, damageSource: DamageSource?) {
        if (livingEntity != null) {
            if (livingEntity.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE)) {
                val isEthereal = EtherealEntityAttachment.getData(livingEntity).isEthereal
                if (!isEthereal) {
                    addNecro(livingEntity)
                }
            }

            if (livingEntity.type == EntityType.VILLAGER) {
                val attacker = damageSource?.entity as? Player
                if (attacker != null) {
                    val lichLevel = AfflictionPlayerAttachment.getData(attacker).getLevel(AfflictionTypes.LICHDOM)
                    if (lichLevel >= 6) {
                        val serverLevel = livingEntity.level() as? ServerLevel ?: return
                        val data = NecromancerLevelAttachment.getData(serverLevel)

                        val exactPos = livingEntity.blockPosition()
                        val newData = NecromancerLevelAttachment.Necro(
                            exactPos,
                            EntityType.ZOMBIE_VILLAGER,
                            livingEntity.level().gameTime
                        )

                        val newList = data.necroList.toMutableList()
                        newList.add(newData)
                        data.necroList = newList

                        NecromancerLevelAttachment.setData(serverLevel, data)
                    }
                }
            }
        }
    }

    private fun calculateDespawnTime(lichLevel: Int): Long {
        return when (lichLevel) {
            1 -> 1200L  // 1 minute
            2 -> 2400L  // 2 minutes
            3 -> 3600L  // 3 minutes
            4 -> 4800L  // 4 minutes
            5 -> 6000L  // 5 minutes
            6 -> 9000L  // 7.5 minutes
            7 -> 12000L // 10 minutes
            8 -> 18000L // 15 minutes
            9 -> 24000L // 20 minutes (full day)
            10 -> -1L   // Permanent
            else -> 600L // 30 seconds fallback
        }
    }

    fun summonNecroAroundPos(level: ServerLevel, summoner: Player, center: BlockPos, radius: Int) {
        val list = collectNecroLists(level, center, radius)
        if (list.isEmpty()) return

        val lichLevel = AfflictionPlayerAttachment.getData(summoner).getLevel(AfflictionTypes.LICHDOM)
        val despawnTime = calculateDespawnTime(lichLevel)

        for ((pos, entityType) in list) {
            val entity = entityType.create(level) as? LivingEntity ?: continue

            entity.moveTo(
                pos.x + 0.5,
                pos.y.toDouble(),
                pos.z + 0.5,
                level.random.nextFloat() * 360f,
                0f
            )

            EtherealEntityAttachment.setData(
                entity,
                EtherealEntityAttachment.Data(
                    summoner.uuid,
                    canDropLoot = false,
                    isEthereal = true,
                    summonTime = level.gameTime,
                    maxLifeTime = despawnTime
                )
            )

            level.addFreshEntity(entity)
            removeNecro(level, pos)
        }
    }


    fun removeNecro(serverLevel: ServerLevel, pos: BlockPos) {
        val data = NecromancerLevelAttachment.getData(serverLevel)
        data.necroList.removeIf {
            it.pos?.x == pos.x && it.pos.y == pos.y && it.pos.z == pos.z
        }
        NecromancerLevelAttachment.setData(serverLevel, data)
    }

    /**
     * Collects necromantic entities within radius of center position
     */
    fun collectNecroLists(level: ServerLevel, center: BlockPos, radius: Int): List<Pair<BlockPos, EntityType<*>>> {
        val data = NecromancerLevelAttachment.getData(level)
        val radiusSquared = radius * radius

        return data.necroList.mapNotNull { entry ->
            val type = entry.entityType
            val pos = entry.pos ?: return@mapNotNull null

            if (type == null) {
                return@mapNotNull null
            }

            val dx = pos.x - center.x
            val dy = pos.y - center.y
            val dz = pos.z - center.z
            val distanceSquared = dx * dx + dy * dy + dz * dz

            if (distanceSquared <= radiusSquared) {
                Pair(pos, type)
            } else {
                null
            }
        }
    }
}