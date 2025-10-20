package dev.sterner.witchery.features.necromancy

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType

object NecromancerLevelAttachment {
    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.NECRO_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.NECRO_DATA_ATTACHMENT, data)
    }

    data class Data(var necroList: MutableList<DeadEntityData> = mutableListOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("necro_data")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    DeadEntityData.CODEC.listOf().fieldOf("dead_entity").forGetter { it.necroList },
                ).apply(instance) { globalPos ->
                    Data(globalPos.toMutableList())
                }
            }
        }
    }

    data class DeadEntityData(
        val pos: BlockPos? = null,
        val entityType: EntityType<*>? = null,
        val timestamp: Long? = null,
    ) {

        companion object {
            val CODEC: Codec<DeadEntityData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter { it.entityType },
                    Codec.LONG.fieldOf("timestamp").forGetter { it.timestamp },
                ).apply(instance) { globalPos, entityType, timestamp ->
                    DeadEntityData(globalPos, entityType, timestamp)
                }
            }
        }
    }
}