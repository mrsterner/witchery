package dev.sterner.witchery.platform.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncVoodooDataS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VoodooPoppetLivingEntityAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setPoppetData(livingEntity: LivingEntity, data: VoodooPoppetData) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPoppetData(livingEntity: LivingEntity): VoodooPoppetData {
        throw AssertionError()
    }

    fun sync(player: Player, data: VoodooPoppetData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncVoodooDataS2CPacket(player, data)
            )
        }
    }

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
}