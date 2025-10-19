package dev.sterner.witchery.core.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.network.SyncBindingCurseS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor

object BindingCurseAttachment {

    fun getData(livingEntity: LivingEntity): Data {
        return livingEntity.getData(WitcheryDataAttachments.BINDING_CURSE)
    }

    fun setData(livingEntity: LivingEntity, data: Data) {
        livingEntity.setData(WitcheryDataAttachments.BINDING_CURSE, data)
        if (livingEntity is Player) {
            sync(livingEntity)
        }
    }

    fun sync(player: Player) {
        if (player is ServerPlayer) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncBindingCurseS2CPayload(player, getData(player))
            )
        }
    }

    fun tick(livingEntity: LivingEntity) {
        if (livingEntity.level().isClientSide) return

        val data = getData(livingEntity)
        if (!data.isActive) return

        val centerPos = data.centerPos
        val radius = data.radius

        val isOutsideBox = livingEntity.x < centerPos.x - radius + 1 ||
                livingEntity.x > centerPos.x + radius ||
                livingEntity.y < centerPos.y - radius + 1 ||
                livingEntity.y > centerPos.y + radius ||
                livingEntity.z < centerPos.z - radius + 1 ||
                livingEntity.z > centerPos.z + radius

        if (isOutsideBox) {
            val direction = Vec3(
                centerPos.x + 0.5 - livingEntity.x,
                centerPos.y + 0.5 - livingEntity.y,
                centerPos.z + 0.5 - livingEntity.z
            ).normalize()

            val pullStrength = 0.5

            livingEntity.deltaMovement = livingEntity.deltaMovement.add(
                direction.x * pullStrength,
                direction.y * pullStrength * 0.5,
                direction.z * pullStrength
            )
            livingEntity.hurtMarked = true

            if (livingEntity.level() is ServerLevel) {
                (livingEntity.level() as ServerLevel).sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    livingEntity.x,
                    livingEntity.y + 1.0,
                    livingEntity.z,
                    5,
                    0.3, 0.3, 0.3,
                    0.05
                )

                if (livingEntity.level().gameTime % 40 == 0L) {
                    livingEntity.level().playSound(
                        null,
                        livingEntity.blockPosition(),
                        SoundEvents.CHAIN_BREAK,
                        SoundSource.PLAYERS,
                        0.5f, 1.5f
                    )
                }
            }
        }

        val newDuration = data.duration - 1

        if (newDuration <= 0) {
            val newData = Data(
                centerPos = centerPos,
                radius = radius,
                duration = 0,
                isActive = false
            )
            setData(livingEntity, newData)
        } else {
            val newData = Data(
                centerPos = centerPos,
                radius = radius,
                duration = newDuration,
                isActive = true
            )
            setData(livingEntity, newData)
        }
    }

    data class Data(
        val centerPos: BlockPos = BlockPos.ZERO,
        val radius: Double = 0.0,
        val duration: Int = 0,
        val isActive: Boolean = false
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("centerPos").forGetter { it.centerPos },
                    Codec.DOUBLE.fieldOf("radius").forGetter { it.radius },
                    Codec.INT.fieldOf("duration").forGetter { it.duration },
                    Codec.BOOL.fieldOf("isActive").forGetter { it.isActive }
                ).apply(instance, ::Data)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Data> = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                Data::centerPos,
                ByteBufCodecs.DOUBLE,
                Data::radius,
                ByteBufCodecs.VAR_INT,
                Data::duration,
                ByteBufCodecs.BOOL,
                Data::isActive,
                ::Data
            )
        }
    }
}