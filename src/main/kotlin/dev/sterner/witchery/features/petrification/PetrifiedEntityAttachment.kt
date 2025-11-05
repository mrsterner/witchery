package dev.sterner.witchery.features.petrification

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.network.SyncPetrificationS2CPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.neoforged.neoforge.network.PacketDistributor

object PetrifiedEntityAttachment {

    data class Data(
        val petrified: Boolean = false,
        val petrificationTicks: Int = 0,
        val totalDuration: Int = 0,
        val age: Float = 0f,
        val limbSwing: Float = 0f,
        val limbSwingAmount: Float = 0f,
        val headYaw: Float = 0f,
        val headPitch: Float = 0f,
        val yBodyRot: Float = 0f,
        val breakProgress: Int = 0,
        val playerPunchCount: Int = 0
    ) {
        fun isPetrified(): Boolean = petrified && petrificationTicks > 0

        fun tick(): Data {
            if (!petrified || petrificationTicks <= 0) {
                return copy(
                    petrified = false,
                    breakProgress = 0,
                    playerPunchCount = 0
                )
            }

            val newTicks = petrificationTicks - 1

            return copy(
                petrificationTicks = newTicks
            )
        }

        fun withPetrification(
            duration: Int,
            age: Float,
            limbSwing: Float,
            limbSwingAmount: Float,
            headYaw: Float,
            headPitch: Float,
            yBodyRot: Float
        ): Data {
            return copy(
                petrified = true,
                petrificationTicks = duration,
                totalDuration = duration,
                age = age,
                limbSwing = limbSwing,
                limbSwingAmount = limbSwingAmount,
                headYaw = headYaw,
                headPitch = headPitch,
                yBodyRot = yBodyRot,
                breakProgress = 0,
                playerPunchCount = 0
            )
        }

        fun incrementBreakProgress(): Data {
            val newProgress = (breakProgress + 1).coerceAtMost(9)
            return copy(breakProgress = newProgress)
        }

        fun incrementPunchCount(): Data {
            return copy(playerPunchCount = playerPunchCount + 1)
        }

        fun getBreakStage(): Int {
            return breakProgress.coerceIn(0, 9)
        }

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("petrified").forGetter { it.petrified },
                    Codec.INT.fieldOf("petrificationTicks").forGetter { it.petrificationTicks },
                    Codec.INT.fieldOf("totalDuration").forGetter { it.totalDuration },
                    Codec.FLOAT.fieldOf("age").forGetter { it.age },
                    Codec.FLOAT.fieldOf("limbSwing").forGetter { it.limbSwing },
                    Codec.FLOAT.fieldOf("limbSwingAmount").forGetter { it.limbSwingAmount },
                    Codec.FLOAT.fieldOf("headYaw").forGetter { it.headYaw },
                    Codec.FLOAT.fieldOf("headPitch").forGetter { it.headPitch },
                    Codec.FLOAT.optionalFieldOf("yBodyRot", 0f).forGetter { it.yBodyRot },
                    Codec.INT.optionalFieldOf("breakProgress", 0).forGetter { it.breakProgress },
                    Codec.INT.optionalFieldOf("playerPunchCount", 0).forGetter { it.playerPunchCount }
                ).apply(instance, ::Data)
            }
        }
    }

    fun getData(entity: LivingEntity): Data {
        return entity.getData(WitcheryDataAttachments.PETRIFIED_ENTITY)
    }

    fun setData(entity: LivingEntity, data: Data) {
        entity.setData(WitcheryDataAttachments.PETRIFIED_ENTITY, data)
        if (entity.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, SyncPetrificationS2CPayload(entity, data))
        }
    }
}