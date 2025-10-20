package dev.sterner.witchery.features.ritual

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity

object RainingToadAttachment {

    @JvmStatic
    fun getData(livingEntity: LivingEntity): Data {
        return livingEntity.getData(WitcheryDataAttachments.RAINING_TOAD_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: Data) {
        livingEntity.setData(WitcheryDataAttachments.RAINING_TOAD_DATA_ATTACHMENT, data)
    }

    class Data(
        var isPoisonous: Boolean = false,
        var safeFall: Boolean = false
    ) {

        fun copy(isPoisonous: Boolean = this.isPoisonous, safeFall: Boolean = this.safeFall): Data {
            return Data(isPoisonous, safeFall)
        }

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isPoisonous").forGetter { it.isPoisonous },
                    Codec.BOOL.fieldOf("safeFall").forGetter { it.safeFall }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("raining_toad_entity_data")
        }
    }
}
