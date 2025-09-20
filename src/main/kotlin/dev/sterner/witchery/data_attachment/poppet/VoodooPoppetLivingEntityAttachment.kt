package dev.sterner.witchery.data_attachment.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncVoodooDataS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VoodooPoppetLivingEntityAttachment {

    @JvmStatic
    fun setPoppetData(livingEntity: LivingEntity, data: VoodooPoppetLivingEntityAttachment.VoodooPoppetData) {
        livingEntity.setData(WitcheryNeoForgeAttachmentRegistry.VOODOO_POPPET_DATA_ATTACHMENT, data)
        if (livingEntity is Player) {
            VoodooPoppetLivingEntityAttachment.sync(livingEntity, data)
        }
    }

    @JvmStatic
    fun getPoppetData(livingEntity: LivingEntity): VoodooPoppetLivingEntityAttachment.VoodooPoppetData {
        return livingEntity.getData(WitcheryNeoForgeAttachmentRegistry.VOODOO_POPPET_DATA_ATTACHMENT)
    }

    fun sync(player: Player, data: VoodooPoppetData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncVoodooDataS2CPayload(player, data)
            )
        }
    }

    data class VoodooPoppetData(
        val isUnderWater: Boolean,
        val underWaterTicks: Int = 0
    ) {

        companion object {
            val CODEC: Codec<VoodooPoppetData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isUnderWater").forGetter { it.isUnderWater },
                    Codec.INT.fieldOf("underWaterTicks").forGetter { it.underWaterTicks },
                ).apply(instance, ::VoodooPoppetData)
            }

            val ID: ResourceLocation = Witchery.id("voodoo_poppet_data")
        }
    }
}