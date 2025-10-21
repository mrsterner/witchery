package dev.sterner.witchery.features.lifeblood

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.network.SyncLifebloodS2CPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object LifebloodPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.LIFEBLOOD_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.LIFEBLOOD_ATTACHMENT, data)
        sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncLifebloodS2CPayload(player, data)
            )
        }
    }

    data class Data(
        var lifebloodPoints: Int = 0,
        var lastRegenTick: Long = 0
    ) {
        companion object {
            val ID: ResourceLocation = Witchery.id("lifeblood")
            const val POINTS_PER_HEART = 5
            const val REGEN_INTERVAL_TICKS = 100

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("lifebloodPoints").forGetter { it.lifebloodPoints },
                    Codec.LONG.optionalFieldOf("lastRegenTick", 0L).forGetter { it.lastRegenTick }
                ).apply(instance, ::Data)
            }
        }

        /**
         * Gets the number of full hearts (each heart = 5 points)
         */
        fun getFullHearts(): Int = lifebloodPoints / POINTS_PER_HEART

        /**
         * Gets the remainder points in the current partial heart
         */
        fun getPartialHeartPoints(): Int = lifebloodPoints % POINTS_PER_HEART

        /**
         * Gets the maximum points this data can regenerate to
         */
        fun getMaxRegenPoints(): Int {
            if (lifebloodPoints == 0) return 0
            val currentHeartLevel = ((lifebloodPoints - 1) / POINTS_PER_HEART) + 1
            return currentHeartLevel * POINTS_PER_HEART
        }

        /**
         * Checks if lifeblood can regenerate
         */
        fun canRegenerate(): Boolean {
            return lifebloodPoints > 0 && lifebloodPoints < getMaxRegenPoints()
        }

        /**
         * Add lifeblood points
         */
        fun addPoints(amount: Int): Data {
            return copy(lifebloodPoints = (lifebloodPoints + amount).coerceAtLeast(0))
        }

        /**
         * Remove lifeblood points
         */
        fun removePoints(amount: Int): Data {
            return copy(lifebloodPoints = (lifebloodPoints - amount).coerceAtLeast(0))
        }
    }
}