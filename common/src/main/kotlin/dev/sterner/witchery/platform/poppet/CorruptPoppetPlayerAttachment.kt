package dev.sterner.witchery.platform.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncCorruptPoppetS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object CorruptPoppetPlayerAttachment {

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
                SyncCorruptPoppetS2CPacket(player, data)
            )
        }
    }

    data class Data(
        val corruptedPoppetCount: Int = 0,
        val corruptedPoppets: MutableSet<ResourceLocation> = mutableSetOf()
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("corruptedPoppetCount").forGetter { it.corruptedPoppetCount },
                    ResourceLocation.CODEC.listOf().xmap(
                        { it.toMutableSet() },
                        { it.toList() }
                    ).fieldOf("corruptedPoppets").forGetter { it.corruptedPoppets }
                ).apply(instance, ::Data)
            }
            val ID: ResourceLocation = Witchery.id("corrupt_poppet_data")
        }
    }
}