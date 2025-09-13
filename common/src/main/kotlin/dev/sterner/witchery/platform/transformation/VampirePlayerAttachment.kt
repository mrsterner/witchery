package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncVampireS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import java.util.*

object VampirePlayerAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(player: Player, data: Data, sync: Boolean = true) {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), SyncVampireS2CPayload(player, data))
        }
    }

    data class Data(
        private val vampireLevel: Int = 0,
        val killedBlazes: Int = 0,
        val usedSunGrenades: Int = 0,
        val villagersHalfBlood: MutableList<UUID> = mutableListOf(),
        val nightTicker: Int = 0,
        val visitedVillages: MutableList<Long> = mutableListOf(),
        val trappedVillagers: MutableList<UUID> = mutableListOf(),
        val abilityIndex: Int = -1,
        val inSunTick: Int = 0,
        val isNightVisionActive: Boolean = false,
        val isSpeedBoostActive: Boolean = false,
        val isBatFormActive: Boolean = false,
        val maxInSunTickClient: Int = 0,
        val abilityCooldowns: MutableMap<String, Int> = mutableMapOf()
    ) {

        fun getVampireLevel(): Int = vampireLevel

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("vampireLevel").forGetter { it.vampireLevel },
                    Codec.INT.fieldOf("killedBlazes").forGetter { it.killedBlazes },
                    Codec.INT.fieldOf("usedSunGrenades").forGetter { it.usedSunGrenades },
                    UUIDUtil.CODEC.listOf().fieldOf("villagersHalfBlood").forGetter { it.villagersHalfBlood },
                    Codec.INT.fieldOf("nightTicker").forGetter { it.nightTicker },
                    Codec.LONG.listOf().fieldOf("visitedVillages").forGetter { it.visitedVillages },
                    UUIDUtil.CODEC.listOf().fieldOf("trappedVillagers").forGetter { it.trappedVillagers },
                    Codec.INT.fieldOf("abilityIndex").forGetter { it.abilityIndex },
                    Codec.INT.fieldOf("inSunTick").forGetter { it.inSunTick },
                    Codec.BOOL.fieldOf("isNightVisionActive").forGetter { it.isNightVisionActive },
                    Codec.BOOL.fieldOf("isSpeedBoostActive").forGetter { it.isSpeedBoostActive },
                    Codec.BOOL.fieldOf("isBatFormActive").forGetter { it.isBatFormActive },
                    Codec.INT.fieldOf("maxInSunTickClient").forGetter { it.maxInSunTickClient },
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("abilityCooldowns").forGetter { it.abilityCooldowns }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("vampire_player_data")
        }
    }
}