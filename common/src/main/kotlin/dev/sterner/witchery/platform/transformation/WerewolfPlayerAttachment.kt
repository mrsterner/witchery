package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncWerewolfS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import java.util.UUID

object WerewolfPlayerAttachment {

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
            WitcheryPayloads.sendToPlayers(player.level(), SyncWerewolfS2CPacket(player, data))
        }
    }

    data class Data(
        val lycanSourceUUID: UUID? = null,
        val werewolfLevel: Int = 0,
        val hasGivenGold: Boolean = false,
        val killedSheep: Int = 0,
        val killedWolves: Int = 0,
        val killHornedOne: Boolean = false,
        val airSlayMonster: Int = 0,
        val nightHowl: Int = 0,
        val wolfPack: Int = 0,
        val pigmenKilled: Int = 0,
        val spreadLycantropy: Boolean = false,
        val abilityIndex: Int = -1,
        val isWolfManFormActive: Boolean = false,
        val isWolfFormActive: Boolean = false,

        ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.fieldOf("lycanSourceUUID").forGetter(Data::lycanSourceUUID),
                    Codec.INT.fieldOf("vampireLevel").forGetter { it.werewolfLevel },
                    Codec.BOOL.fieldOf("hasGivenGold").forGetter { it.hasGivenGold },
                    Codec.INT.fieldOf("killedBlazes").forGetter { it.killedSheep },
                    Codec.INT.fieldOf("killedWolves").forGetter { it.killedWolves },
                    Codec.BOOL.fieldOf("killHornedOne").forGetter { it.killHornedOne },
                    Codec.INT.fieldOf("airSlayMonster").forGetter { it.airSlayMonster },
                    Codec.INT.fieldOf("nightHowl").forGetter { it.nightHowl },
                    Codec.INT.fieldOf("wolfPack").forGetter { it.wolfPack },
                    Codec.INT.fieldOf("pigmenKilled").forGetter { it.pigmenKilled },
                    Codec.BOOL.fieldOf("spreadLycantropy").forGetter { it.spreadLycantropy },
                    Codec.INT.fieldOf("abilityIndex").forGetter { it.abilityIndex },
                    Codec.BOOL.fieldOf("isWolfManFormActive").forGetter { it.isWolfManFormActive },
                    Codec.BOOL.fieldOf("isWolfFormActive").forGetter { it.isWolfFormActive },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("werewolf_player_data")
        }
    }
}