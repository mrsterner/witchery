package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncSoulS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object SoulPoolPlayerAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(entity: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(entity: Player, data: Data) {
        throw AssertionError()
    }

    fun sync(entity: Player, data: Data) {
        if (entity.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(entity.level(), SyncSoulS2CPayload(entity, data))
        }
    }

    data class Data(
        val maxSouls: Int = 0,
        val soulPool: Int = 0
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("maxSouls").forGetter { it.maxSouls },
                    Codec.INT.fieldOf("soulPool").forGetter { it.soulPool }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("soul_pool_data")
        }
    }
}