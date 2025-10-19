package dev.sterner.witchery.core.data_attachment.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncVoodooDataS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object VoodooPoppetLivingEntityAttachment {

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: Data) {
        livingEntity.setData(WitcheryDataAttachments.VOODOO_POPPET_DATA_ATTACHMENT, data)
        if (livingEntity is Player) {
            sync(livingEntity, data)
        }
    }

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): Data {
        return livingEntity.getData(WitcheryDataAttachments.VOODOO_POPPET_DATA_ATTACHMENT)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncVoodooDataS2CPayload(player, data)
            )
        }
    }

    data class Data(
        val isUnderWater: Boolean,
        val underWaterTicks: Int = 0
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isUnderWater").forGetter { it.isUnderWater },
                    Codec.INT.fieldOf("underWaterTicks").forGetter { it.underWaterTicks },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("voodoo_poppet_data")
        }
    }
}