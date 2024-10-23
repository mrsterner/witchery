package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

class LightInfusionData(val isInvisible: Boolean = false, val invisibleTimer: Int = 0) {

    companion object {
        val CODEC: Codec<LightInfusionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL.fieldOf("isInvisible").forGetter { it.isInvisible },
                Codec.INT.fieldOf("invisibleTimer").forGetter { it.invisibleTimer }
            ).apply(instance, ::LightInfusionData)
        }

        val ID: ResourceLocation = Witchery.id("light_infusion_player_data")
    }
}