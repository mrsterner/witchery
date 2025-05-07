package dev.sterner.witchery.platform.transformation

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.payload.SpawnSmokeParticlesS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.AABB
import java.util.*
import java.util.stream.Stream

object VampireChildrenHuntLevelAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(serverLevel: ServerLevel): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(serverLevel: ServerLevel, data: Data) {
        throw AssertionError()
    }

    class HuntData(
        val entityNbt: CompoundTag,
        val coffinPos: BlockPos,
        val creationPos: BlockPos,
    ) {

        companion object {
            val CODEC: Codec<HuntData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    CompoundTag.CODEC.fieldOf("entityNbt").forGetter { it.entityNbt },
                    BlockPos.CODEC.fieldOf("coffinPos").forGetter { it.coffinPos },
                    BlockPos.CODEC.fieldOf("creationPos").forGetter { it.creationPos },
                ).apply(instance, ::HuntData)
            }
        }
    }

    data class Data(val data: MutableMap<UUID, MutableList<HuntData>> = mutableMapOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("vampire_hunt_level_data")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(
                        Codecs.UUID,
                        HuntData.CODEC.listOf().xmap({ it.toMutableList() }, { it.toList() })
                    ).fieldOf("data").forGetter { it.data }
                ).apply(instance, ::Data)
            }
        }
    }
}