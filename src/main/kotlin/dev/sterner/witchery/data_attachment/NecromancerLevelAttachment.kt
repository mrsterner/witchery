package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryDataAttachments
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

    data class Data(var necroList: MutableList<Necro> = mutableListOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("necro_data")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Necro.CODEC.listOf().fieldOf("global_pos").forGetter { it.necroList },
                ).apply(instance) { globalPos ->
                    Data(globalPos.toMutableList())
                }
            }
        }
    }

    data class Necro(
        val pos: BlockPos? = null,
        val entityType: EntityType<*>? = null,
        val timestamp: Long? = null,
    ) {

        companion object {
            val CODEC: Codec<Necro> = RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter { it.entityType },
                    Codec.LONG.fieldOf("timestamp").forGetter { it.timestamp },
                ).apply(instance) { globalPos, entityType, timestamp ->
                    Necro(globalPos, entityType, timestamp)
                }
            }
        }
    }
}