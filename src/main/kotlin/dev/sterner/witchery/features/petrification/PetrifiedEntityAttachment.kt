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
        val headPitch: Float = 0f
    ) {
        fun isPetrified(): Boolean = petrified && petrificationTicks > 0

        fun tick(): Data {
            if (!petrified || petrificationTicks <= 0) {
                return copy(
                    petrified = false
                )
            }

            val newTicks = petrificationTicks - 1

            return copy(
                petrificationTicks = newTicks
            )
        }

        fun withPetrification(duration: Int, age: Float, limbSwing: Float, limbSwingAmount: Float, headYaw: Float, headPitch: Float): Data {
            return copy(
                petrified = true,
                petrificationTicks = duration,
                totalDuration = duration,
                age,
                limbSwing,
                limbSwingAmount,
                headYaw,
                headPitch
            )
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
                    Codec.FLOAT.fieldOf("headPitch").forGetter { it.headPitch }
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