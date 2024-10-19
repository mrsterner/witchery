package dev.sterner.witchery.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState

data class AltarAugment(val type: ResourceLocation, val augments: Map<List<BlockState>, Double>) {
    fun test(state: BlockState) {

    }

    companion object {
        val CODEC = RecordCodecBuilder.create { instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(AltarAugment::type),
            Codec.unboundedMap(BlockState.CODEC.listOf(), Codec.DOUBLE).fieldOf("augments").forGetter(AltarAugment::augments)
        ).apply(instance, ::AltarAugment) }
    }
}