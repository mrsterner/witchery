package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncInfernalInfusionS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object InfernalInfusionPlayerAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setData(player: Player, data: Data) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncInfernalInfusionS2CPacket(player, data)
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