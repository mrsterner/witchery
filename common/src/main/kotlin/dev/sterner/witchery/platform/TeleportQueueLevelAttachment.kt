package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel

object TeleportQueueLevelAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(level: ServerLevel): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        throw AssertionError()
    }



    data class Data(val pendingTeleports: MutableList<TeleportRequest> = mutableListOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("teleport_queue")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(TeleportRequest.CODEC).fieldOf("pendingTeleports")
                        .forGetter { it.pendingTeleports }
                ).apply(instance, ::Data)
            }
        }
    }
}