package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncEtherealS2CPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import java.util.*

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
            val serverLevel = living.level() as ServerLevel

            val packet = SyncEtherealS2CPayload(living.id, data)

            val players = serverLevel.server.playerList.players
            for (player in players) {
                if (player.level() == serverLevel) {
                    NetworkManager.sendToPlayer(player, packet)
                }
            }
        }
    }

    data class Data(
        val ownerUUID: UUID? = null,
        val canDropLoot: Boolean = true,
        val isEthereal: Boolean = false,
        val summonTime: Long = 0,
        val maxLifeTime: Long = 0
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codecs.UUID.optionalFieldOf("ownerUUID").forGetter { Optional.ofNullable(it.ownerUUID) },
                    Codec.BOOL.fieldOf("canDropLoot").forGetter { it.canDropLoot },
                    Codec.BOOL.fieldOf("isEthereal").forGetter { it.isEthereal },
                    Codec.LONG.fieldOf("summonTime").forGetter { it.summonTime },
                    Codec.LONG.fieldOf("maxLifeTime").forGetter { it.maxLifeTime }
                ).apply(instance) { ownerUUIDOptional, canDropLoot, isEthereal, summonTime, maxLifeTime ->
                    Data(
                        ownerUUIDOptional.orElse(null),
                        canDropLoot,
                        isEthereal,
                        summonTime,
                        maxLifeTime,
                    )
                }
            }

            val ID: ResourceLocation = Witchery.id("ethereal")
        }
    }

}