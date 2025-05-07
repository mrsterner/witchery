package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.platform.NecromancerLevelAttachment
import dev.sterner.witchery.platform.EtherealEntityAttachment
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity

object NecroHandler {

    private const val MINECRAFT_DAY = 24000

    fun registerEvents() {
        EntityEvent.LIVING_DEATH.register(::onDeath)
        TickEvent.SERVER_LEVEL_POST.register(::processListExhaustion)
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
            data.necroList.clear()
            data.necroList.addAll(filteredList)
            NecromancerLevelAttachment.setData(serverLevel, data)
        }
    }


    fun addNecro(entity: LivingEntity) {
        val serverLevel = entity.level() as? ServerLevel ?: return
        val data = NecromancerLevelAttachment.getData(serverLevel)

        val pos = entity.blockPosition()
        val newData = NecromancerLevelAttachment.Data(pos, entity.type, entity.level().gameTime)

        data.necroList.add(newData)
        NecromancerLevelAttachment.setData(serverLevel, data)
    }

    private fun onDeath(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity != null) {

            if (livingEntity.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE)) {
                addNecro(livingEntity)
            }
        }

        return EventResult.pass()
    }

    fun summonNecroAroundPos(level: ServerLevel, center: BlockPos, radius: Int) {
        val list = collectNecroLists(level, center, radius)
        for ((pos, entityType) in list) {
            val entity = entityType.create(level) as? LivingEntity ?: continue

            entity.moveTo(
                pos.x + 0.5,
                pos.y.toDouble(),
                pos.z + 0.5,
                level.random.nextFloat() * 360f,
                0f
            )
            
            EtherealEntityAttachment.setData(entity, EtherealEntityAttachment.Data(false))
            
            level.addFreshEntity(entity)

            removeNecro(level, pos)
        }
    }


    fun removeNecro(serverLevel: ServerLevel, pos: BlockPos) {
        val data = NecromancerLevelAttachment.getData(serverLevel)
        data.necroList.removeIf { it.pos == pos }
        NecromancerLevelAttachment.setData(serverLevel, data)
    }

    fun collectNecroLists(level: ServerLevel, center: BlockPos, radius: Int): List<Pair<BlockPos, EntityType<*>>> {
        val data = NecromancerLevelAttachment.getData(level)

        return data.necroList.mapNotNull { entry ->
            val type = entry.entityType
            val pos = entry.pos

            if (pos != null && type != null) {

                val dx = pos.x - center.x
                val dy = pos.y - center.y
                val dz = pos.z - center.z

                if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                    Pair(pos, type)
                } else null
            } else null
        }
    }
}