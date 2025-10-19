package dev.sterner.witchery.core.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.SyncBloodS2CPayload
import dev.sterner.witchery.network.SyncOtherBloodS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object BloodPoolLivingEntityAttachment {

    @JvmStatic
    fun getData(livingEntity: LivingEntity): Data {
        return livingEntity.getData(WitcheryDataAttachments.BLOOD_LIVING_ENTITY_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: Data) {
        livingEntity.setData(WitcheryDataAttachments.BLOOD_LIVING_ENTITY_DATA_ATTACHMENT, data)
        sync(livingEntity, data)
    }

    fun sync(livingEntity: LivingEntity, data: Data) {
        if (livingEntity.level() is ServerLevel) {
            if (livingEntity is Player) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                    livingEntity,
                    SyncBloodS2CPayload(livingEntity, data)
                )
            } else {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                    livingEntity,
                    SyncOtherBloodS2CPayload(livingEntity, data)
                )
            }
        }
    }

    //300 blood = 1 full blood drop
    class Data(val maxBlood: Int = 0, val bloodPool: Int = 0) {

        fun copy(maxBlood: Int = this.maxBlood, bloodPool: Int = this.bloodPool): Data {
            return Data(maxBlood, bloodPool)
        }

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("maxBlood").forGetter { it.maxBlood },
                    Codec.INT.fieldOf("bloodPool").forGetter { it.bloodPool },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.Companion.id("blood_pool_entity_data")
        }
    }
}