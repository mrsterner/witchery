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