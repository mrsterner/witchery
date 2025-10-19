package dev.sterner.witchery.features.familiar

import dev.sterner.witchery.content.entity.OwlEntity
import dev.sterner.witchery.core.data_attachment.FamiliarLevelAttachment
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.frog.Frog
import java.util.UUID

object FamiliarHandler {


    /**
     * Checks if a given entity is currently bound as a familiar in the world.
     */
    fun isBound(level: ServerLevel, entity: LivingEntity): Boolean {
        return FamiliarLevelAttachment.getData(level).familiarList.any { it.familiar == entity.uuid }
    }

    /**
     * Binds a familiar entity to a player, replacing any existing familiar the player had.
     */
    fun bindOwnerAndFamiliar(level: ServerLevel, playerUUID: UUID, familiar: LivingEntity) {
        val oldData = FamiliarLevelAttachment.getData(level)
        val updatedFamiliarSet = oldData.familiarList.toMutableSet()

        updatedFamiliarSet.removeIf { it.owner == playerUUID }
        val tag = CompoundTag()
        familiar.saveAsPassenger(tag)

        updatedFamiliarSet.add(FamiliarLevelAttachment.FamiliarData(playerUUID, familiar.uuid, tag, !familiar.isAlive))

        FamiliarLevelAttachment.setData(level, FamiliarLevelAttachment.Data(updatedFamiliarSet))
    }

    /**
     * Attempts to resurrect a dead familiar for the given player at the specified location.
     */
    fun resurrectDeadFamiliar(level: ServerLevel, playerUUID: UUID, blockPos: BlockPos): Boolean {
        val data = FamiliarLevelAttachment.getData(level)
        val familiarData = data.familiarList.find { it.owner == playerUUID && it.dead }

        if (familiarData != null) {
            val entityTag = familiarData.entityTag

            if (!entityTag.contains("id")) {
                return false
            }

            val resurrectedFamiliar = EntityType.loadEntityRecursive(entityTag, level) { entity ->
                entity.moveTo(blockPos.x + 0.5, blockPos.y.toDouble(), blockPos.z + 0.5)
                entity
            }

            if (resurrectedFamiliar != null) {
                level.addFreshEntity(resurrectedFamiliar)

                val updatedFamiliarSet = data.familiarList.toMutableSet()
                updatedFamiliarSet.remove(familiarData)
                updatedFamiliarSet.add(familiarData.copy(dead = false))

                FamiliarLevelAttachment.setData(level, FamiliarLevelAttachment.Data(updatedFamiliarSet))
                return true
            }
        }
        return false
    }

    /**
     * Retrieves the entity type of the living familiar bound to a player, if available.
     */
    fun getFamiliarEntityType(playerUUID: UUID, level: ServerLevel): EntityType<*>? {
        val familiarData = FamiliarLevelAttachment.getData(level).familiarList.find { it.owner == playerUUID }

        familiarData?.let { familiar ->
            if (familiar.dead) {
                return null
            }

            val familiarEntity = level.getEntity(familiar.familiar)

            return if (familiarEntity is LivingEntity) familiarEntity.type as EntityType<*> else null
        }

        return null
    }

    /**
     * Handles the death of a familiar entity, marking it as dead in the stored data.
     */
    fun familiarDeath(livingEntity: LivingEntity?, damageSource: DamageSource?) {
        if (livingEntity is OwlEntity || livingEntity is Frog || livingEntity is Cat) {

            val level = livingEntity.level() as? ServerLevel ?: return

            val familiarUUID = livingEntity.uuid
            val data = FamiliarLevelAttachment.getData(level)


            data.familiarList.find { it.familiar == familiarUUID }?.let { familiarData ->
                val updatedFamiliarSet = data.familiarList.toMutableSet()

                updatedFamiliarSet.remove(familiarData)
                updatedFamiliarSet.add(familiarData.copy(dead = true))
                FamiliarLevelAttachment.setData(level, FamiliarLevelAttachment.Data(updatedFamiliarSet))
            }
        }

    }


}