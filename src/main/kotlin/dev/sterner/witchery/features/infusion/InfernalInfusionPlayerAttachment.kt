package dev.sterner.witchery.features.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.SyncInfernalInfusionS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object InfernalInfusionPlayerAttachment {

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
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