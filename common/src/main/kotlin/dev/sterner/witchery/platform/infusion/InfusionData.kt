package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation

data class InfusionData(val type: InfusionType = InfusionType.NONE, val charge: Int = MAX_CHARGE) {

    companion object {
        val MAX_CHARGE = 256
        val CODEC: Codec<InfusionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                InfusionType.CODEC.fieldOf("type").forGetter { it.type },
                Codec.INT.fieldOf("charge").forGetter { it.charge }
            ).apply(instance, ::InfusionData)
        }

        val ID: ResourceLocation = Witchery.id("infusion_player_data")
    }
}