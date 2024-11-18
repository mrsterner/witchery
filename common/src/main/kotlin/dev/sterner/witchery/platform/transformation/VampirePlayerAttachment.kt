package dev.sterner.witchery.platform.transformation

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncVampireS2CPacket
import dev.sterner.witchery.payload.VampireAbilitySelectionC2SPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import dev.sterner.witchery.handler.vampire.VampireLeveling
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import java.util.UUID

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
            WitcheryPayloads.sendToPlayers(player.level(), SyncVampireS2CPacket(player, data))
        }
    }

    //Misc Vampire logic

    @JvmStatic
    fun increaseInSunTick(player: Player) {
        val data = getData(player)
        val newInSunTick = (data.inSunTick + 1).coerceAtMost(20 * 5)
        setData(player, data.copy(inSunTick = newInSunTick))
    }

    @JvmStatic
    fun decreaseInSunTick(player: Player) {
        val data = getData(player)
        val newInSunTick = (data.inSunTick - 1).coerceAtLeast(0)
        setData(player, data.copy(inSunTick = newInSunTick))
    }

    //Vampire Ability Section

    @JvmStatic
    fun setAbilityIndex(player: Player, abilityIndex: Int) {
        updateAbilityIndex(player, abilityIndex)
        NetworkManager.sendToServer(VampireAbilitySelectionC2SPayload(abilityIndex))
    }

    @JvmStatic
    fun getAbilities(player: Player): List<VampireAbility> {
        val level = getData(player).vampireLevel
        return VampireAbility.entries.filter { it.unlockLevel <= level }
    }

    @JvmStatic
    fun setNightVision(player: Player, active: Boolean) {
        val data = getData(player)
        val newNightVisionData = data.copy(isNightVisionActive = active)
        setData(player, newNightVisionData)
    }

    @JvmStatic
    fun toggleNightVision(player: Player) {
        setNightVision(player, !getData(player).isNightVisionActive)
    }

    @JvmStatic
    fun setSpeedBoost(player: Player, active: Boolean) {
        val data = getData(player)
        val newSpeedBoostData = data.copy(isSpeedBoostActive = active)
        setData(player, newSpeedBoostData)
    }

    @JvmStatic
    fun toggleSpeedBoost(player: Player) {
        setSpeedBoost(player, !getData(player).isSpeedBoostActive)
    }

    @JvmStatic
    fun setBatForm(player: Player, active: Boolean) {
        val data = getData(player)
        val newBatFormData = data.copy(isBatFormActive = active)
        setData(player, newBatFormData)
    }

    @JvmStatic
    fun toggleBatForm(player: Player) {
        setBatForm(player, !getData(player).isBatFormActive)
    }

    @JvmStatic
    fun updateAbilityIndex(player: Player, index: Int) {
        val data = getData(player)
        setData(player, data.copy(abilityIndex = index))
    }

    data class Data(
        val vampireLevel: Int = 0,
        val killedBlazes: Int = 0,
        val usedSunGrenades: Int = 0,
        val villagersHalfBlood: MutableList<UUID> = mutableListOf(),
        val nightTicker: Int = 0,
        val visitedVillages: MutableList<Long> = mutableListOf(),
        val trappedVillagers: Int = 0,
        val abilityIndex: Int = -1,
        val inSunTick: Int = 0,
        val isNightVisionActive: Boolean = false,
        val isSpeedBoostActive: Boolean = false,
        val isBatFormActive: Boolean = false,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("vampireLevel").forGetter { it.vampireLevel },
                    Codec.INT.fieldOf("killedBlazes").forGetter { it.killedBlazes },
                    Codec.INT.fieldOf("usedSunGrenades").forGetter { it.usedSunGrenades },
                    Codecs.UUID.listOf().fieldOf("villagersHalfBlood").forGetter { it.villagersHalfBlood },
                    Codec.INT.fieldOf("nightTicker").forGetter { it.nightTicker },
                    Codec.LONG.listOf().fieldOf("visitedVillages").forGetter { it.visitedVillages },
                    Codec.INT.fieldOf("trappedVillagers").forGetter { it.trappedVillagers },
                    Codec.INT.fieldOf("abilityIndex").forGetter { it.abilityIndex },
                    Codec.INT.fieldOf("inSunTick").forGetter { it.inSunTick },
                    Codec.BOOL.fieldOf("isNightVisionActive").forGetter { it.isNightVisionActive },
                    Codec.BOOL.fieldOf("isSpeedBoostActive").forGetter { it.isSpeedBoostActive },
                    Codec.BOOL.fieldOf("isBatFormActive").forGetter { it.isBatFormActive },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("vampire_player_data")
        }
    }

    enum class VampireAbility(val unlockLevel: Int) : StringRepresentable {
        DRINK_BLOOD(1),
        TRANSFIX(2),
        SPEED(4),
        BAT_FORM(7);

        override fun getSerializedName(): String {
            return name.lowercase()
        }
    }
}