package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.payload.SelectiveSyncAfflictionS2CPayload
import dev.sterner.witchery.payload.SyncAfflictionS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player
import java.util.Optional
import java.util.UUID

object AfflictionPlayerAttachment {

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
            WitcheryPayloads.sendToPlayers(player.level(), SyncAfflictionS2CPayload(player, data))
        }
    }

    fun selectiveSync(player: Player, data: Data, fields: Set<SyncField>) {
        if (player.level() is ServerLevel && fields.isNotEmpty()) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                SelectiveSyncAfflictionS2CPayload(player, data, fields)
            )
        }
    }

    /**
     * Smart batch updater that automatically tracks and syncs only changed fields
     *
     * @param player The player to update
     * @param sync Whether to sync at all (default true)
     * @param forceFullSync Force full sync instead of selective (default false)
     * @param operations The operations to perform on the data
     */
    inline fun batchUpdate(
        player: Player,
        sync: Boolean = true,
        forceFullSync: Boolean = false,
        crossinline operations: Data.() -> Data
    ): Data {
        val currentData = getData(player)

        currentData.clearDirtyFields()

        val newData = currentData.operations()

        setData(player, newData, sync = false)

        if (sync && player.level() is ServerLevel) {
            val dirtyFields = newData.getDirtyFields()

            when {
                forceFullSync || dirtyFields.isEmpty() -> {
                    sync(player, newData)
                }

                else -> {
                    selectiveSync(player, newData, dirtyFields)
                }
            }
        }

        newData.clearDirtyFields()

        return newData
    }

    /**
     * Batch update with manual field specification (for special cases)
     */
    inline fun batchUpdateWithFields(
        player: Player,
        syncFields: Set<SyncField>? = null,
        crossinline operations: Data.() -> Data
    ): Data {
        val currentData = getData(player)
        val newData = currentData.operations()
        setData(player, newData, sync = false)

        val fieldsToSync = syncFields ?: newData.getDirtyFields()
        if (fieldsToSync.isNotEmpty()) {
            selectiveSync(player, newData, fieldsToSync)
        }

        newData.clearDirtyFields()
        return newData
    }

    /**
     * Update without any sync (for batching multiple players)
     */
    inline fun updateNoSync(
        player: Player,
        crossinline operations: Data.() -> Data
    ): Data {
        val currentData = getData(player)
        val newData = currentData.operations()
        setData(player, newData, sync = false)
        return newData
    }

    /**
     * Sync only the dirty fields from existing data
     */
    fun syncDirtyFields(player: Player) {
        val data = getData(player)
        val dirtyFields = data.getDirtyFields()

        if (dirtyFields.isNotEmpty()) {
            selectiveSync(player, data, dirtyFields)
            data.clearDirtyFields()
        }
    }

    enum class SyncField {
        AFFLICTION_LEVELS,
        ABILITY_INDEX,
        ABILITY_COOLDOWNS,
        VAMP_COMBAT_STATS,  // killedBlazes, usedSunGrenades
        VAMP_FORM_STATES,   // batForm, nightVision, speedBoost
        VAMP_VILLAGES,      // visitedVillages, villagersHalfBlood, trappedVillagers
        WERE_COMBAT_STATS,  // killedSheep, killedWolves, etc.
        WERE_FORM_STATES    // wolfForm, wolfManForm
    }

    // =================
    // DATA CLASS
    // =================

    data class Data(
        private val afflictionLevels: MutableMap<AfflictionTypes, Int> = mutableMapOf(),
        private val abilityIndex: Int = -1,
        private val abilityCooldowns: MutableMap<String, Int> = mutableMapOf(),
        private val vampData: VampData = VampData(),
        private val wereData: WereData = WereData(),
        private val selectedAbilities: List<String> = emptyList(),
        @Transient private var dirtyFields: MutableSet<SyncField> = mutableSetOf()
    ) {

        fun markDirty(field: SyncField) {
            dirtyFields.add(field)
        }

        fun getDirtyFields(): Set<SyncField> = dirtyFields.toSet()

        fun clearDirtyFields() {
            dirtyFields.clear()
        }

        fun getLevel(type: AfflictionTypes): Int = afflictionLevels.getOrDefault(type, 0)

        fun getWerewolfLevel(): Int = afflictionLevels.getOrDefault(AfflictionTypes.LYCANTHROPY, 0)
        fun getVampireLevel(): Int = afflictionLevels.getOrDefault(AfflictionTypes.VAMPIRISM, 0)

        fun setLevel(type: AfflictionTypes, level: Int): Data {
            val newLevels = afflictionLevels.toMutableMap()
            if (level <= 0) {
                newLevels.remove(type)
            } else {
                newLevels[type] = level
            }
            return copy(afflictionLevels = newLevels).apply {
                markDirty(SyncField.AFFLICTION_LEVELS)
            }
        }

        fun getAbilityIndex(): Int = abilityIndex
        fun withAbilityIndex(index: Int): Data = copy(abilityIndex = index).apply {
            markDirty(SyncField.ABILITY_INDEX)
        }

        fun getAbilityCooldown(ability: String): Int = abilityCooldowns.getOrDefault(ability, 0)
        fun withAbilityCooldown(ability: String, cooldown: Int): Data {
            val newCooldowns = abilityCooldowns.toMutableMap()
            if (cooldown <= 0) {
                newCooldowns.remove(ability)
            } else {
                newCooldowns[ability] = cooldown
            }
            return copy(abilityCooldowns = newCooldowns).apply {
                markDirty(SyncField.ABILITY_COOLDOWNS)
            }
        }
        fun withoutAbilityCooldown(ability: String): Data = copy(
            abilityCooldowns = abilityCooldowns.toMutableMap().apply { remove(ability) }
        ).apply {
            markDirty(SyncField.ABILITY_COOLDOWNS)
        }

        fun getSelectedAbilities(): List<String> = selectedAbilities.take(5)

        fun withSelectedAbilities(abilities: List<String>): Data =
            copy(selectedAbilities = abilities.take(5)).apply {
                markDirty(SyncField.ABILITY_INDEX)
            }

        fun addSelectedAbility(abilityId: String): Data {
            if (selectedAbilities.size >= 5 || selectedAbilities.contains(abilityId)) {
                return this
            }
            return copy(selectedAbilities = selectedAbilities + abilityId).apply {
                markDirty(SyncField.ABILITY_INDEX)
            }
        }

        fun removeSelectedAbility(abilityId: String): Data =
            copy(selectedAbilities = selectedAbilities - abilityId).apply {
                markDirty(SyncField.ABILITY_INDEX)
            }

        fun clearSelectedAbilities(): Data =
            copy(selectedAbilities = emptyList()).apply {
                markDirty(SyncField.ABILITY_INDEX)
            }

        // ----------------
        // VampData helpers
        // ----------------

        fun getKilledBlazes(): Int = vampData.killedBlazes
        fun getUsedSunGrenades(): Int = vampData.usedSunGrenades
        fun getNightTicker(): Int = vampData.getNightTicker()
        fun getInSunTick(): Int = vampData.getInSunTick()
        fun hasNightVision(): Boolean = vampData.isNightVisionActive
        fun hasSpeedBoost(): Boolean = vampData.isSpeedBoostActive
        fun isBatForm(): Boolean = vampData.isBatFormActive
        fun getMaxInSunTickClient(): Int = vampData.getMaxInSunTickClient()
        fun getVisitedVillages(): List<Long> = vampData.visitedVillages
        fun getVillagersHalfBlood(): List<UUID> = vampData.villagersHalfBlood
        fun getTrappedVillagers(): List<UUID> = vampData.trappedVillagers

        // --- VampData Mutators (copy) ---
        fun withKilledBlazes(killed: Int): Data = copy(vampData = vampData.copy(killedBlazes = killed)).apply {
            markDirty(SyncField.VAMP_COMBAT_STATS)
        }
        fun withUsedSunGrenades(used: Int): Data = copy(vampData = vampData.copy(usedSunGrenades = used)).apply {
            markDirty(SyncField.VAMP_COMBAT_STATS)
        }
        fun withNightTicker(ticks: Int): Data = copy(vampData = vampData.setNightTicker(ticks))

        fun withInSunTick(ticks: Int, maxTicks: Int? = null): Data {
            val clamped = if (maxTicks != null) {
                ticks.coerceIn(0, maxTicks)
            } else {
                ticks.coerceAtLeast(0)
            }
            return copy(vampData = vampData.setInSunTick(clamped)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }
        }

        fun incrementInSunTick(by: Int = 1, maxTicks: Int? = null): Data {
            val newValue = vampData.getInSunTick() + by
            val clamped = if (maxTicks != null) {
                newValue.coerceIn(0, maxTicks)
            } else {
                newValue.coerceAtLeast(0)
            }
            return copy(vampData = vampData.setInSunTick(clamped)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }
        }

        fun decrementInSunTick(by: Int = 1): Data {
            val newValue = (vampData.getInSunTick() - by).coerceAtLeast(0)
            return copy(vampData = vampData.setInSunTick(newValue)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }
        }

        fun incrementNightTicker(): Data = copy(
            vampData = vampData.setNightTicker(vampData.getNightTicker() + 1)
        ).apply {
            markDirty(SyncField.VAMP_FORM_STATES)
        }

        fun withNightVision(active: Boolean): Data = copy(vampData = vampData.copy(isNightVisionActive = active)).apply {
            markDirty(SyncField.VAMP_FORM_STATES)
        }
        fun withSpeedBoost(active: Boolean): Data = copy(vampData = vampData.copy(isSpeedBoostActive = active)).apply {
            markDirty(SyncField.VAMP_FORM_STATES)
        }
        fun withBatForm(active: Boolean): Data = copy(vampData = vampData.copy(isBatFormActive = active)).apply {
            markDirty(SyncField.VAMP_FORM_STATES)
        }
        fun withMaxInSunTickClient(value: Int): Data = copy(vampData = vampData.copy(maxInSunTickClient = value))

        // --- List Mutators ---
        fun addVisitedVillage(pos: Long): Data = copy(vampData = vampData.copy(
            visitedVillages = vampData.visitedVillages + pos
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun removeVisitedVillage(pos: Long): Data = copy(vampData = vampData.copy(
            visitedVillages = vampData.visitedVillages - pos
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun clearVisitedVillages(): Data = copy(vampData = vampData.copy(
            visitedVillages = emptyList()
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun addVillagerHalfBlood(uuid: UUID): Data = copy(vampData = vampData.copy(
            villagersHalfBlood = vampData.villagersHalfBlood + uuid
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun removeVillagerHalfBlood(uuid: UUID): Data = copy(vampData = vampData.copy(
            villagersHalfBlood = vampData.villagersHalfBlood - uuid
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun clearVillagerHalfBlood(): Data = copy(vampData = vampData.copy(
            villagersHalfBlood = emptyList()
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun addTrappedVillager(uuid: UUID): Data = copy(vampData = vampData.copy(
            trappedVillagers = vampData.trappedVillagers + uuid
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun removeTrappedVillager(uuid: UUID): Data = copy(vampData = vampData.copy(
            trappedVillagers = vampData.trappedVillagers - uuid
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun clearTrappedVillager(): Data = copy(vampData = vampData.copy(
            trappedVillagers = emptyList()
        )).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }
        fun incrementKilledBlazes(by: Int = 1): Data = copy(vampData = vampData.copy(killedBlazes = vampData.killedBlazes + by)).apply {
            markDirty(SyncField.VAMP_COMBAT_STATS)
        }
        fun clearKilledBlazes(): Data = copy(vampData = vampData.copy(killedBlazes = 0)).apply {
            markDirty(SyncField.VAMP_COMBAT_STATS)
        }
        fun incrementUsedSunGrenades(by: Int = 1): Data = copy(vampData = vampData.copy(usedSunGrenades = vampData.usedSunGrenades + by)).apply {
            markDirty(SyncField.VAMP_COMBAT_STATS)
        }
        fun clearUsedSunGrenades(): Data = copy(vampData = vampData.copy(usedSunGrenades = 0)).apply {
            markDirty(SyncField.VAMP_COMBAT_STATS)
        }

        // ----------------
        // WereData helpers
        // ----------------

        fun getLycanSource(): Optional<UUID> = wereData.lycanSourceUUID
        fun hasGivenGold(): Boolean = wereData.hasGivenGold
        fun getKilledSheep(): Int = wereData.killedSheep
        fun getKilledWolves(): Int = wereData.killedWolves
        fun hasKilledHornedOne(): Boolean = wereData.killHornedOne
        fun getAirSlayMonster(): Int = wereData.airSlayMonster
        fun getNightHowl(): Int = wereData.nightHowl
        fun getWolfPack(): Int = wereData.wolfPack
        fun getPigmenKilled(): Int = wereData.pigmenKilled
        fun hasSpreadLycanthropy(): Boolean = wereData.spreadLycanthropy
        fun isWolfManForm(): Boolean = wereData.isWolfManFormActive
        fun isWolfForm(): Boolean = wereData.isWolfFormActive

        fun withLycanSource(src: Optional<UUID>): Data = copy(wereData = wereData.copy(lycanSourceUUID = src))
        fun withGivenGold(given: Boolean): Data = copy(wereData = wereData.copy(hasGivenGold = given))
        fun withKilledSheep(kills: Int): Data = copy(wereData = wereData.copy(killedSheep = kills)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun withKilledWolves(kills: Int): Data = copy(wereData = wereData.copy(killedWolves = kills)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun withKilledHornedOne(killed: Boolean): Data = copy(wereData = wereData.copy(killHornedOne = killed)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun withAirSlayMonster(count: Int): Data = copy(wereData = wereData.copy(airSlayMonster = count)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun withNightHowl(count: Int): Data = copy(wereData = wereData.copy(nightHowl = count)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun withWolfPack(count: Int): Data = copy(wereData = wereData.copy(wolfPack = count)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun withPigmenKilled(kills: Int): Data = copy(wereData = wereData.copy(pigmenKilled = kills)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun withSpreadLycanthropy(spread: Boolean): Data = copy(wereData = wereData.copy(spreadLycanthropy = spread))
        fun withWolfManForm(active: Boolean): Data = copy(wereData = wereData.copy(isWolfManFormActive = active)).apply {
            markDirty(SyncField.WERE_FORM_STATES)
        }
        fun withWolfForm(active: Boolean): Data = copy(wereData = wereData.copy(isWolfFormActive = active)).apply {
            markDirty(SyncField.WERE_FORM_STATES)
        }

        fun incrementKilledSheep(by: Int = 1): Data = copy(wereData = wereData.copy(killedSheep = wereData.killedSheep + by)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun incrementKilledWolves(by: Int = 1): Data = copy(wereData = wereData.copy(killedWolves = wereData.killedWolves + by)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun incrementAirSlayMonster(by: Int = 1): Data = copy(wereData = wereData.copy(airSlayMonster = wereData.airSlayMonster + by)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun incrementNightHowl(by: Int = 1): Data = copy(wereData = wereData.copy(nightHowl = wereData.nightHowl + by)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun incrementWolfPack(by: Int = 1): Data = copy(wereData = wereData.copy(wolfPack = wereData.wolfPack + by)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }
        fun incrementPigmenKilled(by: Int = 1): Data = copy(wereData = wereData.copy(pigmenKilled = wereData.pigmenKilled + by)).apply {
            markDirty(SyncField.WERE_COMBAT_STATS)
        }

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(
                        StringRepresentable.fromEnum(AfflictionTypes::values),
                        Codec.INT
                    ).fieldOf("afflictionLevels").forGetter { it.afflictionLevels },
                    Codec.INT.fieldOf("abilityIndex").forGetter { it.abilityIndex },
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("abilityCooldowns").forGetter { it.abilityCooldowns },
                    VampData.CODEC.fieldOf("vampData").forGetter { it.vampData },
                    WereData.CODEC.fieldOf("wereData").forGetter { it.wereData },
                    Codec.STRING.listOf().optionalFieldOf("selectedAbilities", emptyList()).forGetter { it.selectedAbilities },
                    ).apply(instance) { levels, index, cooldowns, vamp, were, sel ->
                    Data(levels, index, cooldowns, vamp, were, sel)
                }
            }

            val ID: ResourceLocation = Witchery.id("affliction_player_data")
        }
    }

    // ----------------
    // Vampire data
    // ----------------
    data class VampData(
        val killedBlazes: Int = 0,
        val usedSunGrenades: Int = 0,
        val villagersHalfBlood: List<UUID> = emptyList(),
        private val nightTicker: Int = 0,
        val visitedVillages: List<Long> = emptyList(),
        val trappedVillagers: List<UUID> = emptyList(),
        private val inSunTick: Int = 0,
        val isNightVisionActive: Boolean = false,
        val isSpeedBoostActive: Boolean = false,
        val isBatFormActive: Boolean = false,
        private val maxInSunTickClient: Int = 0
    ) {

        fun getInSunTick(): Int = inSunTick.coerceAtLeast(0)
        fun getNightTicker(): Int = nightTicker.coerceAtLeast(0)
        fun getMaxInSunTickClient(): Int = maxInSunTickClient.coerceAtLeast(0)

        fun setInSunTick(value: Int): VampData {
            val clamped = value.coerceAtLeast(0)
            return copy(inSunTick = clamped)
        }

        fun setNightTicker(value: Int): VampData {
            val clamped = value.coerceAtLeast(0)
            return copy(nightTicker = clamped)
        }

        companion object {
            val CODEC: Codec<VampData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("killedBlazes").forGetter { it.killedBlazes },
                    Codec.INT.fieldOf("usedSunGrenades").forGetter { it.usedSunGrenades },
                    UUIDUtil.CODEC.listOf().fieldOf("villagersHalfBlood").forGetter { it.villagersHalfBlood },
                    Codec.INT.fieldOf("nightTicker").forGetter { it.nightTicker },
                    Codec.LONG.listOf().fieldOf("visitedVillages").forGetter { it.visitedVillages },
                    UUIDUtil.CODEC.listOf().fieldOf("trappedVillagers").forGetter { it.trappedVillagers },
                    Codec.INT.fieldOf("inSunTick").forGetter { it.inSunTick },
                    Codec.BOOL.fieldOf("isNightVisionActive").forGetter { it.isNightVisionActive },
                    Codec.BOOL.fieldOf("isSpeedBoostActive").forGetter { it.isSpeedBoostActive },
                    Codec.BOOL.fieldOf("isBatFormActive").forGetter { it.isBatFormActive },
                    Codec.INT.fieldOf("maxInSunTickClient").forGetter { it.maxInSunTickClient }
                ).apply(instance, ::VampData)
            }

            val ID: ResourceLocation = Witchery.id("vampire_player_data")
        }
    }

    // ----------------
    // Werewolf data
    // ----------------
    data class WereData(
        val lycanSourceUUID: Optional<UUID> = Optional.empty(),
        val hasGivenGold: Boolean = false,
        val killedSheep: Int = 0,
        val killedWolves: Int = 0,
        val killHornedOne: Boolean = false,
        val airSlayMonster: Int = 0,
        val nightHowl: Int = 0,
        val wolfPack: Int = 0,
        val pigmenKilled: Int = 0,
        val spreadLycanthropy: Boolean = false,
        val isWolfManFormActive: Boolean = false,
        val isWolfFormActive: Boolean = false
    ) {
        companion object {
            val CODEC: Codec<WereData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("lycanSourceUUID").forGetter { it.lycanSourceUUID },
                    Codec.BOOL.fieldOf("hasGivenGold").forGetter { it.hasGivenGold },
                    Codec.INT.fieldOf("killedSheep").forGetter { it.killedSheep },
                    Codec.INT.fieldOf("killedWolves").forGetter { it.killedWolves },
                    Codec.BOOL.fieldOf("killHornedOne").forGetter { it.killHornedOne },
                    Codec.INT.fieldOf("airSlayMonster").forGetter { it.airSlayMonster },
                    Codec.INT.fieldOf("nightHowl").forGetter { it.nightHowl },
                    Codec.INT.fieldOf("wolfPack").forGetter { it.wolfPack },
                    Codec.INT.fieldOf("pigmenKilled").forGetter { it.pigmenKilled },
                    Codec.BOOL.fieldOf("spreadLycanthropy").forGetter { it.spreadLycanthropy },
                    Codec.BOOL.fieldOf("isWolfManFormActive").forGetter { it.isWolfManFormActive },
                    Codec.BOOL.fieldOf("isWolfFormActive").forGetter { it.isWolfFormActive }
                ).apply(instance, ::WereData)
            }

            val ID: ResourceLocation = Witchery.id("werewolf_player_data")
        }
    }
}