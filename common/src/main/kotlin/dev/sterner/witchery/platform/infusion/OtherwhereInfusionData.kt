package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

class OtherwhereInfusionData(val teleportHoldTicks: Int = 0, val teleportCooldown: Int = 0) {

    companion object {
        val CODEC: Codec<OtherwhereInfusionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("teleportHoldTicks").forGetter { it.teleportHoldTicks },
                Codec.INT.fieldOf("teleportCooldown").forGetter { it.teleportCooldown }
            ).apply(instance, ::OtherwhereInfusionData)
        }

        val ID: ResourceLocation = Witchery.id("otherwhere_infusion_player_data")

        fun tick(player: Player?) {
            if (player != null && PlayerInfusionDataAttachment.getPlayerInfusion(player).type == InfusionType.OTHERWHERE) {

                val data = OtherwhereInfusionDataAttachment.getInfusion(player)
                val ticks = data.teleportCooldown

                if (ticks <= 0) {
                    OtherwhereInfusionDataAttachment.setInfusion(player, data.teleportHoldTicks, 0)
                } else {
                    OtherwhereInfusionDataAttachment.setInfusion(player, data.teleportHoldTicks, ticks - 1)
                }
            }
        }
    }
}