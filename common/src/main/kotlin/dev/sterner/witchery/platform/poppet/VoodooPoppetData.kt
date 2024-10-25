package dev.sterner.witchery.platform.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation

data class VoodooPoppetData(val isUnderWater: Boolean) {

    companion object {
        val CODEC: Codec<VoodooPoppetData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL.fieldOf("isUnderWater").forGetter { it.isUnderWater },
            ).apply(instance, ::VoodooPoppetData)
        }

        val ID: ResourceLocation = Witchery.id("voodoo_poppet_data")
    }
}