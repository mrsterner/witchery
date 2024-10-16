package dev.sterner.witchery.api.attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation

data class AltarAttachmentData(val altarSet: MutableSet<BlockPos> = mutableSetOf()) {
    companion object {
        val CODEC: Codec<AltarAttachmentData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.list(BlockPos.CODEC).xmap({ it.toMutableSet() }, { it.toList() })
                    .fieldOf("altarSet").forGetter { it.altarSet }
            ).apply(instance, ::AltarAttachmentData)
        }
        val ID: ResourceLocation = Witchery.id("altar_level_data")
    }
}