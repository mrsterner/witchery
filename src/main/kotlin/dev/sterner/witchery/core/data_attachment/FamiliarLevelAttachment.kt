package dev.sterner.witchery.core.data_attachment

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.*

object FamiliarLevelAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.FAMILIAR_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.FAMILIAR_LEVEL_DATA_ATTACHMENT, data)
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