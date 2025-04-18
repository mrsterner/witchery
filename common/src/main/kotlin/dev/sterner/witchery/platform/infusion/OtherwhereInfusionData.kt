package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation

class OtherwhereInfusionData(val teleportHoldTicks: Int = 0, val teleportCooldown: Int = 0) {

    companion object {
        val CODEC: Codec<OtherwhereInfusionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("teleportHoldTicks").forGetter { it.teleportHoldTicks },
                Codec.INT.fieldOf("teleportCooldown").forGetter { it.teleportCooldown }
            ).apply(instance, ::OtherwhereInfusionData)
        }

        val ID: ResourceLocation = Witchery.id("otherwhere_infusion_player_data")
    }
}