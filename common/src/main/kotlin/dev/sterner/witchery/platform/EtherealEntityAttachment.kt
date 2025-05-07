package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncEtherealS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import java.util.UUID

object EtherealEntityAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(livingEntity: LivingEntity): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: Data) {
        throw AssertionError()
    }

    fun sync(living: LivingEntity, data: Data) {
        if (living.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(living.level(), living.blockPosition(), SyncEtherealS2CPacket(living.id, data))
        }
    }

    class Data(var ownerUUID: UUID? = null, var canDropLoot: Boolean = true, var isEthereal: Boolean = false) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codecs.UUID.fieldOf("ownerUUID").forGetter { it.ownerUUID },
                    Codec.BOOL.fieldOf("canDropLoot").forGetter { it.canDropLoot },
                    Codec.BOOL.fieldOf("isEthereal").forGetter { it.isEthereal }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("ethereal")
        }
    }
}