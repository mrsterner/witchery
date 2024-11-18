package dev.sterner.witchery.platform.transformation

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.payload.DismountBroomC2SPayload
import dev.sterner.witchery.payload.SyncVampireS2CPacket
import dev.sterner.witchery.payload.VampireAbilitySelectionC2SPayload
import dev.sterner.witchery.registry.WitcheryEntityAttributes
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
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

    @JvmStatic
    fun setMaxBlood(player: Player){
        val data = getData(player)

        val toSet = when (data.vampireLevel) {
            1 -> 900
            2 -> 1200
            3 -> 1500
            4 -> 1500
            5 -> 1800
            6 -> 2100
            7 -> 2400
            8 -> 2700
            9 -> 3000
            10 -> 3600
            else -> 0
        }

        val bloodData = BloodPoolLivingEntityAttachment.getData(player)

        BloodPoolLivingEntityAttachment.setData(player, BloodPoolLivingEntityAttachment.Data(toSet, bloodData.bloodPool))
    }

    fun setAbilityIndex(player: Player, abilityIndex: Int) {
        updateAbilityIndex(player, abilityIndex)
        NetworkManager.sendToServer(VampireAbilitySelectionC2SPayload(abilityIndex))
    }

    fun getAbilities(player: Player): List<VampireAbility> {
        val level = getData(player).vampireLevel
        return VampireAbility.entries.filter { it.unlockLevel <= level }
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), SyncVampireS2CPacket(player, data))
        }
    }

    val KNOCKBACK_BONUS = AttributeModifier(Witchery.id("vampire_knockback"), 0.5, AttributeModifier.Operation.ADD_VALUE)

    @JvmStatic
    fun increaseVampireLevel(player: ServerPlayer) {
        val data = getData(player)

        val currentLevel = data.vampireLevel
        if (TornPageItem.hasAdvancement(player, TornPageItem.advancementLocations[currentLevel])) {
            setData(player, data.copy(vampireLevel = data.vampireLevel + 1))
            setMaxBlood(player)
            player.sendSystemMessage(Component.literal("Vampire Level Up: ${data.vampireLevel + 1}"))
            updateModifiers(player)
        }
    }

    fun updateModifiers(player: Player) {
        val data = getData(player)
        player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.removeModifier(KNOCKBACK_BONUS)

        if (data.vampireLevel >= 3) {
            player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)
                ?.addPermanentModifier(KNOCKBACK_BONUS)
        }
    }

    @JvmStatic
    fun increaseKilledBlazes(player: Player) {
        val data = getData(player)
        setData(player, data.copy(killedBlazes = data.killedBlazes + 1))
    }

    @JvmStatic
    fun increaseUsedSunGrenades(player: Player) {
        val data = getData(player)
        setData(player, data.copy(usedSunGrenades = data.usedSunGrenades + 1))
    }

    @JvmStatic
    fun increaseVillagersHalfBlood(player: ServerPlayer, villager: Villager) {
        val data = getData(player)
        if (!data.villagersHalfBlood.contains(villager.uuid)) {
            val updatedList = data.villagersHalfBlood.toMutableList().apply { add(villager.uuid) }
            setData(player, data.copy(villagersHalfBlood = updatedList))
            if (getData(player).villagersHalfBlood.size >= 5) {
                if (getData(player).vampireLevel == 2) {
                    increaseVampireLevel(player)
                }
            }
        }
    }

    @JvmStatic
    fun removeVillagerHalfBlood(player: Player, villager: Villager) {
        val data = getData(player)
        if (data.villagersHalfBlood.contains(villager.uuid)) {
            val updatedList = data.villagersHalfBlood.toMutableList().apply { remove(villager.uuid) }
            setData(player, data.copy(villagersHalfBlood = updatedList))
        }
    }

    @JvmStatic
    fun increaseNightTicker(player: Player) {
        val data = getData(player)
        setData(player, data.copy(nightTicker = data.nightTicker + 1), false)
    }

    @JvmStatic
    fun increaseVisitedVillages(player: Player) {
        val data = getData(player)
        setData(player, data.copy(visitedVillages = data.visitedVillages + 1))
    }

    @JvmStatic
    fun increaseTrappedVillagers(player: Player) {
        val data = getData(player)
        setData(player, data.copy(trappedVillagers = data.trappedVillagers + 1))
    }

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

    fun resetNightCounter(player: Player) {
        val data = getData(player)
        setData(player, data.copy(nightTicker = 0))
    }

    data class Data(
        val vampireLevel: Int = 0,
        val killedBlazes: Int = 0,
        val usedSunGrenades: Int = 0,
        val villagersHalfBlood: MutableList<UUID> = mutableListOf(),
        val nightTicker: Int = 0,
        val visitedVillages: Int = 0,
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
                    Codec.INT.fieldOf("visitedVillages").forGetter { it.visitedVillages },
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