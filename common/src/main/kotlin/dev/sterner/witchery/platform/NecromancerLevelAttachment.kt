package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType

object NecromancerLevelAttachment {
    @ExpectPlatform
    @JvmStatic
    fun getData(level: ServerLevel): NecroList {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(level: ServerLevel, data: NecroList) {
        throw AssertionError()
    }

    data class NecroList(var necroList: MutableList<Data> = mutableListOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("necro_data")

            val CODEC: Codec<NecroList> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Data.CODEC.listOf().fieldOf("global_pos").forGetter { it.necroList },
                ).apply(instance) { globalPos ->
                    NecroList(globalPos.toMutableList())
                }
            }
        }
    }

    data class Data(
        val pos: BlockPos? = null,
        val entityType: EntityType<*>? = null,
        val timestamp: Long? = null,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter { it.entityType },
                    Codec.LONG.fieldOf("timestamp").forGetter { it.timestamp },
                ).apply(instance) { globalPos, entityType, timestamp ->
                    Data(globalPos, entityType, timestamp)
                }
            }
        }
    }
}