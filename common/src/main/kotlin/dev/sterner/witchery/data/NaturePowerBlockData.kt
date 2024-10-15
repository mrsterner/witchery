package dev.sterner.witchery.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation

class NaturePowerBlockData(val block: ResourceLocation, val power: Int, val limit: Int) {

    companion object {

        val CODEC: Codec<NaturePowerBlockData> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<NaturePowerBlockData> ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("block").forGetter(NaturePowerBlockData::block),
                    Codec.INT.fieldOf("power").forGetter(NaturePowerBlockData::power),
                    Codec.INT.fieldOf("limit").forGetter(NaturePowerBlockData::limit)
                ).apply(
                    instance
                ) { name, parentCombinations, mutationChance ->
                    NaturePowerBlockData(name, parentCombinations, mutationChance)
                }
            }

        val TAG_CODEC: Codec<NaturePowerBlockData> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<NaturePowerBlockData> ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("tag").forGetter(NaturePowerBlockData::block),
                    Codec.INT.fieldOf("power").forGetter(NaturePowerBlockData::power),
                    Codec.INT.fieldOf("limit").forGetter(NaturePowerBlockData::limit)
                ).apply(
                    instance
                ) { name, parentCombinations, mutationChance ->
                    NaturePowerBlockData(name, parentCombinations, mutationChance)
                }
            }
    }
}