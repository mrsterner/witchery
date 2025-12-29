package dev.sterner.witchery.features.affliction.villager_afflictions

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.npc.Villager

object VillagerDataAttachment {

    @JvmStatic
    fun getData(villager: Villager): Data {
        return villager.getData(WitcheryDataAttachments.VILLAGER_ATTACHMENT)
    }

    @JvmStatic
    fun setData(villager: Villager, data: Data) {
        villager.setData(WitcheryDataAttachments.VILLAGER_ATTACHMENT, data)
    }

    data class Data(
        val infectedTicks: Int = 0,
        val isWerewolf: Boolean = false,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("infectedTicks").forGetter { it.infectedTicks },
                    Codec.BOOL.fieldOf("isWerewolf").forGetter { it.isWerewolf },

                    ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.Companion.id("villager_data")
        }
    }
}