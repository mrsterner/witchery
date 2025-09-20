package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncUnderWaterS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object UnderWaterBreathPlayerAttachment {


    @JvmStatic
    fun getData(player: Player): UnderWaterBreathPlayerAttachment.Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.UNDER_WATER_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: UnderWaterBreathPlayerAttachment.Data) {
        player.setData(WitcheryNeoForgeAttachmentRegistry.UNDER_WATER_PLAYER_DATA_ATTACHMENT, data)
        UnderWaterBreathPlayerAttachment.sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncUnderWaterS2CPayload(player, data))
        }
    }

    fun registerEvents() {
        TickEvent.PLAYER_POST.register { player ->
            val data = getData(player)

            if (data.duration > 0) {
                val newDuration = data.duration - 1

                val newData = Data(
                    duration = newDuration,
                    maxDuration = data.maxDuration
                )

                setData(player, newData)

                if (newDuration % 20 == 0) {
                    sync(player, newData)
                }

                if (newDuration <= 0) {
                    val clearedData = Data(
                        duration = 0,
                        maxDuration = 0
                    )
                    setData(player, clearedData)
                    sync(player, clearedData)
                }

                val isInWater = player.isEyeInFluid(FluidTags.WATER)
                
                if (isInWater) {
                    player.airSupply = player.maxAirSupply
                } else {
                    player.airSupply = player.airSupply - 5
                    if (player.tickCount % 5 == 0) {

                        if (player.airSupply <= -20) {
                            player.airSupply = 0
                            player.hurt(player.damageSources().drown(), 2.0f)
                        }

                        if (player.airSupply < player.maxAirSupply * 0.3 && player.level() is ServerLevel) {
                            val level = player.level() as ServerLevel
                            level.sendParticles(
                                ParticleTypes.BUBBLE,
                                player.x,
                                player.y + player.eyeHeight,
                                player.z,
                                1, 0.1, 0.1, 0.1, 0.05
                            )
                        }
                    }
                }
            }
        }
    }

    data class Data(
        val duration: Int = 0,
        var maxDuration: Int = 0
    ) {
        companion object {
            val ID: ResourceLocation = Witchery.id("player_under_water")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("duration").forGetter { it.duration },
                    Codec.INT.fieldOf("maxDuration").forGetter { it.maxDuration },
                ).apply(instance, ::Data)
            }
        }
    }
}