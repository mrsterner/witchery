package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.teleport.TeleportRequest
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.RegistryFixedCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import java.util.*

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

    data class NecroList(val necroList: MutableList<Data> = mutableListOf()){
        companion object {
            val ID: ResourceLocation = Witchery.id("necro_data")

            val CODEC: Codec<NecroList> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Data.CODEC.listOf().fieldOf("global_pos").forGetter { it.necroList },
                ).apply(instance) { globalPos ->
                    NecroList(globalPos)
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
                ).apply(instance) { globalPos, entityType, timestamp->
                    Data(globalPos, entityType, timestamp)
                }
            }
        }
    }
}