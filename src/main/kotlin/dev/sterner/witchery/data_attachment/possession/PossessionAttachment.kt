package dev.sterner.witchery.data_attachment.possession

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.payload.SyncPlayerPossessionS2CPayload
import dev.sterner.witchery.payload.SyncPossessableS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments.PLAYER_POSSESSION
import dev.sterner.witchery.registry.WitcheryDataAttachments.POSSESSABLE
import dev.sterner.witchery.registry.WitcheryDataAttachments.POSSESSED_DATA
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import java.util.Optional
import java.util.UUID

object PossessionAttachment {

    fun get(player: Player): PlayerPossessionData {
        return player.getData(PLAYER_POSSESSION)
    }

    fun getPossessable(entity: LivingEntity): PossessableData {
        return entity.getData(POSSESSABLE)
    }

    fun getPossessedData(entity: Entity): PossessedEntityData {
        return entity.getData(POSSESSED_DATA)
    }

    fun getHost(possessor: Entity): Mob? {
        if (possessor !is Player) return null
        val data = possessor.getExistingData(PLAYER_POSSESSION).orElse(null) ?: return null
        val hostId = data.possessedEntityId ?: return null

        return if (!possessor.level().isClientSide) {
            (possessor.level() as? ServerLevel)?.getEntity(hostId) as? Mob
        } else {
            if (data.possessedEntityNetworkId != -1) {
                possessor.level().getEntity(data.possessedEntityNetworkId) as? Mob
            } else {
                null
            }
        }
    }

    fun syncToClient(entity: LivingEntity) {
        if (entity.level().isClientSide) return

        val possessableData = entity.getExistingData(POSSESSABLE).orElse(null) ?: return
        val packet = SyncPossessableS2CPayload(entity, possessableData)

        entity.level().players().forEach { player ->
            if (player is ServerPlayer && player.hasLineOfSight(entity)) {
                player.connection.send(packet)
            }
        }
    }

    fun syncPlayerPossession(player: ServerPlayer) {
        val data = player.getData(PLAYER_POSSESSION)
        val packet = SyncPlayerPossessionS2CPayload(player.uuid, data)
        player.connection.send(packet)
    }

    data class PlayerPossessionData(
        var possessedEntityId: UUID? = null,
        var possessedEntityNetworkId: Int = -1,
        var conversionTimer: Int = 0,
        var previousPossessedUuid: UUID? = null,
    ) {
        companion object {
            val CODEC: Codec<PlayerPossessionData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("possessed_entity_id").forGetter { Optional.ofNullable(it.possessedEntityId) },
                    Codec.INT.fieldOf("possessed_entity_network_id").forGetter { it.possessedEntityNetworkId },
                    Codec.INT.fieldOf("conversion_timer").forGetter { it.conversionTimer },
                    UUIDUtil.CODEC.optionalFieldOf("previous_possessed_uuid").forGetter { Optional.ofNullable(it.previousPossessedUuid) }
                ).apply(instance) { possessedId, networkId, timer, prevUuid ->
                    PlayerPossessionData(
                        possessedId.orElse(null),
                        networkId,
                        timer,
                        prevUuid.orElse(null)
                    )
                }
            }
        }

        fun isPossessionOngoing(): Boolean = possessedEntityId != null

        fun isCuring(): Boolean = conversionTimer > 0
    }

    data class PossessableData(
        var possessorId: UUID? = null,
        var previousPossessorId: UUID? = null
    ) {
        companion object {
            val CODEC: Codec<PossessableData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("possessor_id").forGetter { Optional.ofNullable(it.possessorId) },
                    UUIDUtil.CODEC.optionalFieldOf("previous_possessor_id").forGetter { Optional.ofNullable(it.previousPossessorId) }
                ).apply(instance) { possessorId, prevId ->
                    PossessableData(
                        possessorId.orElse(null),
                        prevId.orElse(null)
                    )
                }
            }
        }

        fun isBeingPossessed(): Boolean = possessorId != null
    }

    data class PossessedEntityData(
        var hungerDatai: CompoundTag? = null,
        var inventory: OrderedInventory? = null,
        var selectedSlot: Int = 0,
        var convertedUnderPossession: Boolean = false
    ) {
        companion object {
            val CODEC: Codec<PossessedEntityData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    CompoundTag.CODEC.optionalFieldOf("hunger_data").forGetter { Optional.ofNullable(it.hungerDatai) },
                    Codec.INT.fieldOf("selected_slot").forGetter { it.selectedSlot },
                    Codec.BOOL.fieldOf("converted_under_possession").forGetter { it.convertedUnderPossession }
                    // Note: Inventory needs custom serialization
                ).apply(instance) { hunger, slot, converted ->
                    PossessedEntityData(
                        hunger.orElse(null),
                        null, // Will be handled separately
                        slot,
                        converted
                    )
                }
            }
        }

        fun getHungerData(): CompoundTag {
            if (hungerDatai == null) {
                hungerDatai = CompoundTag().apply {
                    putInt("foodLevel", 20)
                }
            }
            return hungerDatai!!
        }

        fun dropItems(entity: Entity) {
            inventory?.let { inv ->
                for (i in 0 until inv.containerSize) {
                    val stack = inv.removeItem(i, Int.MAX_VALUE)
                    if (!stack.isEmpty) {
                        entity.spawnAtLocation(stack)
                    }
                }
            }
        }
    }
}