package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.payload.SpawnNecroParticlesS2CPayload
import dev.sterner.witchery.payload.SpawnSmokeParticlesS2CPayload
import dev.sterner.witchery.platform.EtherealEntityAttachment
import dev.sterner.witchery.platform.NecromancerLevelAttachment
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryPayloads
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

object NecroHandler {

    private const val MINECRAFT_DAY = 24000
    private const val PARTICLE_SPAWN_RADIUS = 0.5
    private const val PARTICLE_DETECTION_RANGE = 16.0
    private const val PARTICLE_COUNT = 3

    fun registerEvents() {
        EntityEvent.LIVING_DEATH.register(::onDeath)
        TickEvent.SERVER_LEVEL_POST.register(::processListExhaustion)
        TickEvent.SERVER_LEVEL_POST.register(::tick)
    }

    fun tickLiving(livingEntity: LivingEntity) {
        if (livingEntity.level().gameTime % 10 != 0L) return

        if (livingEntity.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE)) {
            val etherealData = EtherealEntityAttachment.getData(livingEntity)
            if (etherealData.isEthereal) {
                EtherealEntityAttachment.sync(livingEntity, etherealData)
            }
        }
    }

    fun tick(serverLevel: ServerLevel?) {
        if (serverLevel == null) {
            return
        }
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
        val bl2 = InfusionPlayerAttachment.getPlayerInfusion(player).type == InfusionType.NECRO
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

            WitcheryPayloads.sendToPlayers(level, SpawnNecroParticlesS2CPayload(Vec3(x, y, z)))
        }
    }

    private fun processListExhaustion(serverLevel: ServerLevel?) {
        if (serverLevel == null) return

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
        val newData = NecromancerLevelAttachment.Data(exactPos, entity.type, entity.level().gameTime)

        val newList = data.necroList.toMutableList()
        newList.add(newData)
        data.necroList = newList

        NecromancerLevelAttachment.setData(serverLevel, data)
    }

    private fun onDeath(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity != null) {
            if (livingEntity.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE)) {
                val isEthereal = EtherealEntityAttachment.getData(livingEntity).isEthereal
                if (!isEthereal) {
                    addNecro(livingEntity)
                }
            }
        }
        return EventResult.pass()
    }

    fun summonNecroAroundPos(level: ServerLevel, summoner: Player, center: BlockPos, radius: Int) {
        val list = collectNecroLists(level, center, radius)
        if (list.isEmpty()) {
            return
        }

        var successCount = 0

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
                    isEthereal = true
                )
            )

            level.addFreshEntity(entity)


            successCount++

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