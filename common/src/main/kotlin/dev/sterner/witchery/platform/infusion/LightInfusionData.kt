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

        fun tick(player: Player?) {

            if (player != null && PlayerInfusionDataAttachment.getPlayerInfusion(player).type == InfusionType.LIGHT) {
                if (LightInfusionDataAttachment.isInvisible(player).isInvisible) {
                    val ticks = LightInfusionDataAttachment.isInvisible(player).invisibleTimer

                    if (ticks <= 0) {
                        LightInfusionDataAttachment.setInvisible(player, false, 0)
                    } else {
                        LightInfusionDataAttachment.setInvisible(player, true, ticks - 1)
                    }
                }
            }
        }
    }


}