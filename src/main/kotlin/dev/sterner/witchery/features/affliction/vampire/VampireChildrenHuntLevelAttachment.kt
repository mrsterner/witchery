package dev.sterner.witchery.features.affliction.vampire

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.UUID

object VampireChildrenHuntLevelAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.VAMPIRE_HUNT_LEVEL_DATA_ATTACHMENT, data)
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
            val ID: ResourceLocation = Witchery.Companion.id("vampire_hunt_level_data")

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