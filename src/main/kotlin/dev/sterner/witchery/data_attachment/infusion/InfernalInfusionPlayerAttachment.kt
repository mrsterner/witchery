package dev.sterner.witchery.data_attachment.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncInfernalInfusionS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments.INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object InfernalInfusionPlayerAttachment {

    @JvmStatic
    fun setData(player: Player, data: InfernalInfusionPlayerAttachment.Data) {
        player.setData(INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT, data)
        InfernalInfusionPlayerAttachment.sync(player, data)
    }

    @JvmStatic
    fun getData(player: Player): InfernalInfusionPlayerAttachment.Data {
        return player.getData(INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncInfernalInfusionS2CPayload(player, data)
            )
        }
    }

    class Data(val currentCreature: CreatureType = CreatureType.NONE) {

        companion object {

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    CreatureType.CODEC.fieldOf("currentCreature").forGetter { it.currentCreature }
                ).apply(instance, ::Data)
            }
            val ID: ResourceLocation = Witchery.id("infernal_infusion_data")
        }

    }
}