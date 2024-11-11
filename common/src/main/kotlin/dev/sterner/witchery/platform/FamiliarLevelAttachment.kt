package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.EventResult
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.OwlEntity
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.frog.Frog
import java.util.*

object FamiliarLevelAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(level: ServerLevel): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        throw AssertionError()
    }

    fun isBound(level: ServerLevel, entity: LivingEntity): Boolean {
        return getData(level).familiarList.any { it.familiar == entity.uuid }
    }

    fun bindOwnerAndFamiliar(level: ServerLevel, playerUUID: UUID, familiar: LivingEntity) {
        val oldData = getData(level)
        val updatedFamiliarSet = oldData.familiarList.toMutableSet()

        updatedFamiliarSet.removeIf { it.owner == playerUUID }
        val tag = CompoundTag()
        familiar.saveAsPassenger(tag)

        updatedFamiliarSet.add(FamiliarData(playerUUID, familiar.uuid, tag, !familiar.isAlive))

        setData(level, Data(updatedFamiliarSet))
    }

    fun resurrectDeadFamiliar(level: ServerLevel, playerUUID: UUID, blockPos: BlockPos) {
        val data = getData(level)
        val familiarData = data.familiarList.find { it.owner == playerUUID && it.dead }

        println("Familiar Data: $familiarData")
        if (familiarData != null) {
            val entityTag = familiarData.entityTag

            // Check if entityTag has "id" to specify entity type
            if (!entityTag.contains("id")) {
                println("Error: Entity tag missing 'id' field for entity type.")
                return
            }

            // Attempt to load the entity
            val resurrectedFamiliar = EntityType.loadEntityRecursive(entityTag, level) { entity ->
                entity.moveTo(blockPos.x + 0.5, blockPos.y.toDouble(), blockPos.z + 0.5)
                entity
            }

            println("Resurrected Familiar: $resurrectedFamiliar")

            if (resurrectedFamiliar != null) {
                level.addFreshEntity(resurrectedFamiliar)

                // Update familiar data to mark as alive
                val updatedFamiliarSet = data.familiarList.toMutableSet()
                updatedFamiliarSet.remove(familiarData)
                updatedFamiliarSet.add(familiarData.copy(dead = false))

                setData(level, Data(updatedFamiliarSet))
            } else {
                println("Error: Failed to resurrect familiar - Entity loading returned null.")
            }
        } else {
            println("No dead familiar found for player $playerUUID")
        }
    }

    fun getFamiliarEntityType(playerUUID: UUID, level: ServerLevel): EntityType<*>? {
        val familiarData = getData(level).familiarList.find { it.owner == playerUUID }

        familiarData?.let { familiar ->
            if (familiar.dead) {
                return null
            }

            val familiarEntity = level.getEntity(familiar.familiar)

            return if (familiarEntity is LivingEntity) familiarEntity.type as EntityType<*> else null
        }

        return null
    }

    fun familiarDeath(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is OwlEntity || livingEntity is Frog || livingEntity is Cat) {

            val level = livingEntity.level() as? ServerLevel ?: return EventResult.pass()

            val familiarUUID = livingEntity.uuid
            println("FUUID: $familiarUUID")
            val data = getData(level)


            data.familiarList.find { it.familiar == familiarUUID }?.let { familiarData ->
                val updatedFamiliarSet = data.familiarList.toMutableSet()

                updatedFamiliarSet.remove(familiarData)
                updatedFamiliarSet.add(familiarData.copy(dead = true))
                println("Set")
                setData(level, Data(updatedFamiliarSet))
            }
        }

        return EventResult.pass()
    }

    data class FamiliarData(val owner: UUID, val familiar: UUID, val entityTag: CompoundTag, val dead: Boolean) {

        companion object {
            val CODEC: Codec<FamiliarData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codecs.UUID.fieldOf("owner").forGetter { it.owner },
                    Codecs.UUID.fieldOf("familiar").forGetter { it.familiar },
                    CompoundTag.CODEC.fieldOf("entity").forGetter { it.entityTag },
                    Codec.BOOL.fieldOf("dead").forGetter { it.dead },
                ).apply(instance, ::FamiliarData)
            }
        }
    }

    data class Data(val familiarList: MutableSet<FamiliarData> = mutableSetOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("familiar_list")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(FamiliarData.CODEC)
                        .fieldOf("familiarList")
                        .forGetter { it.familiarList.toList() }
                ).apply(instance) { familiarList ->
                    Data(familiarList.toMutableSet())
                }
            }
        }
    }
}