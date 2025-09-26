package dev.sterner.witchery.data_attachment.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.payload.SelectiveSyncAfflictionS2CPayload
import dev.sterner.witchery.payload.SyncAfflictionS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments.AFFLICTION_PLAYER_DATA_ATTACHMENT
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*

object AfflictionPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(AFFLICTION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data, sync: Boolean = true) {
        player.setData(AFFLICTION_PLAYER_DATA_ATTACHMENT, data)
        if (sync) {
            sync(player, data)
        }
    }

    @JvmStatic
    fun sync(player: Player, data: Data) {
        if (player is ServerPlayer) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, SyncAfflictionS2CPayload(player, data))
        }
    }

    fun selectiveSync(player: Player, data: Data, fields: Set<SyncField>) {
        if (player.level() is ServerLevel && fields.isNotEmpty()) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SelectiveSyncAfflictionS2CPayload(player, data, fields)
            )
        }
    }

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

    enum class SyncField {
        AFFLICTION_LEVELS,
        ABILITY_INDEX,
        ABILITY_COOLDOWNS,
        VAMP_COMBAT_STATS,  // killedBlazes, usedSunGrenades
        VAMP_FORM_STATES,   // batForm, nightVision, speedBoost
        VAMP_VILLAGES,      // visitedVillages, villagersHalfBlood, trappedVillagers
        WERE_COMBAT_STATS,  // killedSheep, killedWolves, etc.
        WERE_FORM_STATES,    // wolfForm, wolfManForm
        LICH_FORM_STATES,
        LICH_PROGRESS,
        LICH_SOUL,
    }

    // =================
    // DATA CLASS
    // =================

    data class Data(
        private val afflictionLevels: MutableMap<AfflictionTypes, Int> = mutableMapOf(),
        private val abilityIndex: Int = -1,
        private val abilityCooldowns: MutableMap<String, Int> = mutableMapOf(),
        val vampData: VampData = VampData(),
        val wereData: WereData = WereData(),
        val lichData: LichData = LichData(),
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
        fun getLichLevel(): Int = afflictionLevels.getOrDefault(AfflictionTypes.LICHDOM, 0)
        fun getAnyLevel(): Boolean = afflictionLevels.any { it.value > 0 }

        fun setLevel(type: AfflictionTypes, level: Int): Data {
            val newLevels = afflictionLevels.toMutableMap()
            val hadAffliction = newLevels.getOrDefault(type, 0) > 0

            if (level <= 0) {
                newLevels.remove(type)
            } else {
                newLevels[type] = level
            }

            val shouldClearAbilities = (hadAffliction && level <= 0) || (level > 0 && !hadAffliction)

            return if (shouldClearAbilities) {
                copy(
                    afflictionLevels = newLevels,
                    selectedAbilities = emptyList(),
                    abilityIndex = -1
                ).apply {
                    markDirty(SyncField.AFFLICTION_LEVELS)
                    markDirty(SyncField.ABILITY_INDEX)
                }
            } else {
                copy(afflictionLevels = newLevels).apply {
                    markDirty(SyncField.AFFLICTION_LEVELS)
                }
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
        fun getNightTicker(): Int = vampData.nightTicker
        fun getInSunTick(): Int = vampData.inSunTick
        fun hasNightVision(): Boolean = vampData.isNightVisionActive
        fun hasSpeedBoost(): Boolean = vampData.isSpeedBoostActive
        fun isBatForm(): Boolean = vampData.isBatFormActive
        fun getMaxInSunTickClient(): Int = vampData.maxInSunTickClient
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

        fun withNightTicker(ticks: Int): Data =
            copy(vampData = vampData.copy(nightTicker = ticks))


        fun withInSunTick(ticks: Int, maxTicks: Int? = null): Data {
            val clamped = if (maxTicks != null) {
                ticks.coerceIn(0, maxTicks)
            } else {
                ticks.coerceAtLeast(0)
            }
            return copy(vampData = vampData.copy(inSunTick = clamped)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }
        }

        fun incrementInSunTick(by: Int = 1, maxTicks: Int? = null): Data {
            val newValue = vampData.inSunTick + by
            val clamped = if (maxTicks != null) {
                newValue.coerceIn(0, maxTicks)
            } else {
                newValue.coerceAtLeast(0)
            }
            return copy(vampData = vampData.copy(inSunTick = clamped)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }
        }

        fun decrementInSunTick(by: Int = 1): Data {
            val newValue = (vampData.inSunTick - by).coerceAtLeast(0)
            return copy(vampData = vampData.copy(inSunTick = newValue)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }
        }

        fun incrementNightTicker(): Data = copy(
            vampData = vampData.copy(nightTicker = vampData.nightTicker + 1)
        ).apply {
            markDirty(SyncField.VAMP_FORM_STATES)
        }

        fun withNightVision(active: Boolean): Data =
            copy(vampData = vampData.copy(isNightVisionActive = active)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }

        fun withSpeedBoost(active: Boolean): Data = copy(vampData = vampData.copy(isSpeedBoostActive = active)).apply {
            markDirty(SyncField.VAMP_FORM_STATES)
        }

        fun withBatForm(active: Boolean): Data = copy(vampData = vampData.copy(isBatFormActive = active)).apply {
            markDirty(SyncField.VAMP_FORM_STATES)
        }

        fun withMaxInSunTickClient(value: Int): Data =
            copy(vampData = vampData.copy(maxInSunTickClient = value)).apply {
                markDirty(SyncField.VAMP_FORM_STATES)
            }

        // --- List Mutators ---
        fun addVisitedVillage(pos: Long): Data = copy(
            vampData = vampData.copy(
                visitedVillages = vampData.visitedVillages + pos
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun removeVisitedVillage(pos: Long): Data = copy(
            vampData = vampData.copy(
                visitedVillages = vampData.visitedVillages - pos
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun clearVisitedVillages(): Data = copy(
            vampData = vampData.copy(
                visitedVillages = emptyList()
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun addVillagerHalfBlood(uuid: UUID): Data = copy(
            vampData = vampData.copy(
                villagersHalfBlood = vampData.villagersHalfBlood + uuid
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun removeVillagerHalfBlood(uuid: UUID): Data = copy(
            vampData = vampData.copy(
                villagersHalfBlood = vampData.villagersHalfBlood - uuid
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun clearVillagerHalfBlood(): Data = copy(
            vampData = vampData.copy(
                villagersHalfBlood = emptyList()
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun addTrappedVillager(uuid: UUID): Data = copy(
            vampData = vampData.copy(
                trappedVillagers = vampData.trappedVillagers + uuid
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun removeTrappedVillager(uuid: UUID): Data = copy(
            vampData = vampData.copy(
                trappedVillagers = vampData.trappedVillagers - uuid
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun clearTrappedVillager(): Data = copy(
            vampData = vampData.copy(
                trappedVillagers = emptyList()
            )
        ).apply {
            markDirty(SyncField.VAMP_VILLAGES)
        }

        fun incrementKilledBlazes(by: Int = 1): Data =
            copy(vampData = vampData.copy(killedBlazes = vampData.killedBlazes + by)).apply {
                markDirty(SyncField.VAMP_COMBAT_STATS)
            }

        fun clearKilledBlazes(): Data = copy(vampData = vampData.copy(killedBlazes = 0)).apply {
            markDirty(SyncField.VAMP_COMBAT_STATS)
        }

        fun incrementUsedSunGrenades(by: Int = 1): Data =
            copy(vampData = vampData.copy(usedSunGrenades = vampData.usedSunGrenades + by)).apply {
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
        fun withWolfManForm(active: Boolean): Data =
            copy(wereData = wereData.copy(isWolfManFormActive = active)).apply {
                markDirty(SyncField.WERE_FORM_STATES)
            }

        fun withWolfForm(active: Boolean): Data = copy(wereData = wereData.copy(isWolfFormActive = active)).apply {
            markDirty(SyncField.WERE_FORM_STATES)
        }

        fun incrementKilledSheep(by: Int = 1): Data =
            copy(wereData = wereData.copy(killedSheep = wereData.killedSheep + by)).apply {
                markDirty(SyncField.WERE_COMBAT_STATS)
            }

        fun incrementKilledWolves(by: Int = 1): Data =
            copy(wereData = wereData.copy(killedWolves = wereData.killedWolves + by)).apply {
                markDirty(SyncField.WERE_COMBAT_STATS)
            }

        fun incrementAirSlayMonster(by: Int = 1): Data =
            copy(wereData = wereData.copy(airSlayMonster = wereData.airSlayMonster + by)).apply {
                markDirty(SyncField.WERE_COMBAT_STATS)
            }

        fun incrementNightHowl(by: Int = 1): Data =
            copy(wereData = wereData.copy(nightHowl = wereData.nightHowl + by)).apply {
                markDirty(SyncField.WERE_COMBAT_STATS)
            }

        fun incrementWolfPack(by: Int = 1): Data =
            copy(wereData = wereData.copy(wolfPack = wereData.wolfPack + by)).apply {
                markDirty(SyncField.WERE_COMBAT_STATS)
            }

        fun incrementPigmenKilled(by: Int = 1): Data =
            copy(wereData = wereData.copy(pigmenKilled = wereData.pigmenKilled + by)).apply {
                markDirty(SyncField.WERE_COMBAT_STATS)
            }

        // ----------------
        // LichData helpers
        // ----------------
        fun getReadTablets(): List<UUID> = lichData.readTablets
        fun getBoundSouls(): Int = lichData.boundSouls
        fun hasZombieKilledMob(): Boolean = lichData.zombieKilledMob
        fun getKilledGolems(): Int = lichData.killedGolems
        fun getDrainedAnimals(): Int = lichData.drainedAnimals
        fun hasPossessedKillVillager(): Boolean = lichData.possessedKillVillager
        fun hasKilledWither(): Boolean = lichData.killedWither
        fun isPhylacteryBound(): Boolean = lichData.phylacteryBound
        fun getPhylacteryDeaths(): Int = lichData.phylacteryDeaths
        fun getPhylacteryDeathTimes(): List<Long> = lichData.phylacteryDeathTimes
        fun getPhylacterySouls(): Int = lichData.phylacterySouls
        fun isSoulForm(): Boolean = lichData.isSoulFormActive
        fun isVagrant(): Boolean = lichData.isVagrant

        fun getPhylacteryDeathsInOneNight(player: ServerPlayer): Int {
            val oneNightAgo = player.level().gameTime - 12000
            return lichData.phylacteryDeathTimes.count { it > oneNightAgo }
        }

        // Add Lich mutators
        fun addReadTablet(tabletId: UUID): Data = copy(
            lichData = lichData.copy(
                readTablets = lichData.readTablets + tabletId
            )
        ).apply { markDirty(SyncField.LICH_FORM_STATES) }

        fun withReadTablets(list: List<UUID>): Data = copy(lichData = lichData.copy(readTablets = list))
            .apply { markDirty(SyncField.LICH_PROGRESS) }

        fun withGolemKills(count: Int): Data = copy(lichData = lichData.copy(killedGolems = count))
            .apply { markDirty(SyncField.LICH_PROGRESS) }

        fun withDrainedAnimals(count: Int): Data = copy(lichData = lichData.copy(drainedAnimals = count))
            .apply { markDirty(SyncField.LICH_PROGRESS) }

        fun withBoundSouls(count: Int): Data = copy(lichData = lichData.copy(boundSouls = count))
            .apply { markDirty(SyncField.LICH_SOUL) }

        fun withPhylacteryDeaths(count: Int): Data = copy(lichData = lichData.copy(phylacteryDeaths = count))
            .apply { markDirty(SyncField.LICH_SOUL) }

        fun withPhylacteryDeathTimes(count: List<Long>): Data = copy(lichData = lichData.copy(phylacteryDeathTimes = count))
            .apply { markDirty(SyncField.LICH_SOUL) }

        fun incrementBoundSouls(by: Int = 1): Data = copy(
            lichData = lichData.copy(
                boundSouls = lichData.boundSouls + by
            )
        ).apply { markDirty(SyncField.LICH_SOUL) }

        fun withZombieKilledMob(killed: Boolean): Data = copy(
            lichData = lichData.copy(
                zombieKilledMob = killed
            )
        ).apply { markDirty(SyncField.LICH_PROGRESS) }

        fun incrementKilledGolems(by: Int = 1): Data = copy(
            lichData = lichData.copy(
                killedGolems = lichData.killedGolems + by
            )
        ).apply { markDirty(SyncField.LICH_PROGRESS) }

        fun incrementDrainedAnimals(by: Int = 1): Data = copy(
            lichData = lichData.copy(
                drainedAnimals = lichData.drainedAnimals + by
            )
        ).apply { markDirty(SyncField.LICH_PROGRESS) }

        fun withPossessedKillVillager(killed: Boolean): Data = copy(
            lichData = lichData.copy(
                possessedKillVillager = killed
            )
        ).apply { markDirty(SyncField.LICH_PROGRESS) }

        fun withKilledWither(killed: Boolean): Data = copy(
            lichData = lichData.copy(
                killedWither = killed
            )
        ).apply { markDirty(SyncField.LICH_PROGRESS) }

        fun withPhylacteryBound(bound: Boolean): Data = copy(
            lichData = lichData.copy(
                phylacteryBound = bound
            )
        ).apply { markDirty(SyncField.LICH_SOUL) }

        fun incrementPhylacteryDeaths(player: ServerPlayer): Data {
            val gameTime = player.level().gameTime
            return copy(
                lichData = lichData.copy(
                    phylacteryDeaths = lichData.phylacteryDeaths + 1,
                    phylacteryDeathTimes = lichData.phylacteryDeathTimes + gameTime
                )
            ).apply { markDirty(SyncField.LICH_SOUL) }
        }

        fun withPhylacterySouls(souls: Int): Data = copy(
            lichData = lichData.copy(
                phylacterySouls = souls.coerceIn(0, 3)
            )
        ).apply { markDirty(SyncField.LICH_SOUL) }

        fun withSoulForm(active: Boolean): Data = copy(
            lichData = lichData.copy(
                isSoulFormActive = active
            )
        ).apply { markDirty(SyncField.LICH_FORM_STATES) }


        fun withVagrant(active: Boolean): Data = copy(
            lichData = lichData.copy(
                isVagrant = active
            )
        ).apply { markDirty(SyncField.LICH_FORM_STATES) }

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(
                        StringRepresentable.fromEnum(AfflictionTypes::values),
                        Codec.INT
                    ).fieldOf("afflictionLevels").forGetter { it.afflictionLevels },
                    Codec.INT.fieldOf("abilityIndex").forGetter { it.abilityIndex },
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("abilityCooldowns")
                        .forGetter { it.abilityCooldowns },
                    VampData.CODEC.fieldOf("vampData").forGetter { it.vampData },
                    WereData.CODEC.fieldOf("wereData").forGetter { it.wereData },
                    LichData.CODEC.fieldOf("lichData").forGetter { it.lichData },
                    Codec.STRING.listOf().optionalFieldOf("selectedAbilities", emptyList())
                        .forGetter { it.selectedAbilities },
                ).apply(instance) { levels, index, cooldowns, vamp, were, lich, sel ->
                    Data(levels, index, cooldowns, vamp, were, lich, sel)
                }
            }

            val ID: ResourceLocation = Witchery.id("affliction_player_data")
        }
    }

    data class LichData(
        val readTablets: List<UUID> = emptyList(),
        val boundSouls: Int = 0,
        val zombieKilledMob: Boolean = false,
        val killedGolems: Int = 0,
        val drainedAnimals: Int = 0,
        val possessedKillVillager: Boolean = false,
        val killedWither: Boolean = false,
        val phylacteryBound: Boolean = false,
        val phylacteryDeaths: Int = 0,
        val phylacteryDeathTimes: List<Long> = emptyList(),
        val phylacterySouls: Int = 0,
        val isSoulFormActive: Boolean = false,
        val isVagrant: Boolean = false
    ) {
        companion object {
            val CODEC: Codec<LichData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.listOf().fieldOf("readTablets").forGetter { it.readTablets },
                    Codec.INT.fieldOf("boundSouls").forGetter { it.boundSouls },
                    Codec.BOOL.fieldOf("zombieKilledMob").forGetter { it.zombieKilledMob },
                    Codec.INT.fieldOf("killedGolems").forGetter { it.killedGolems },
                    Codec.INT.fieldOf("drainedAnimals").forGetter { it.drainedAnimals },
                    Codec.BOOL.fieldOf("possessedKillVillager").forGetter { it.possessedKillVillager },
                    Codec.BOOL.fieldOf("killedWither").forGetter { it.killedWither },
                    Codec.BOOL.fieldOf("phylacteryBound").forGetter { it.phylacteryBound },
                    Codec.INT.fieldOf("phylacteryDeaths").forGetter { it.phylacteryDeaths },
                    Codec.LONG.listOf().fieldOf("phylacteryDeathTimes").forGetter { it.phylacteryDeathTimes },
                    Codec.INT.fieldOf("phylacterySouls").forGetter { it.phylacterySouls },
                    Codec.BOOL.fieldOf("isSoulFormActive").forGetter { it.isSoulFormActive },
                    Codec.BOOL.fieldOf("isVagrant").forGetter { it.isVagrant }
                ).apply(instance, ::LichData)
            }

            val ID: ResourceLocation = Witchery.id("lich_player_data")
        }
    }


    data class VampData(
        val killedBlazes: Int = 0,
        val usedSunGrenades: Int = 0,
        val isNightVisionActive: Boolean = false,
        val isSpeedBoostActive: Boolean = false,
        val isBatFormActive: Boolean = false,
        var nightTicker: Int = 0,
        val inSunTick: Int = 0,
        val maxInSunTickClient: Int = 0,
        val visitedVillages: List<Long> = listOf(),
        val villagersHalfBlood: List<UUID> = listOf(),
        val trappedVillagers: List<UUID> = listOf()
    ) {
        companion object {
            val CODEC: Codec<VampData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("killedBlazes").forGetter { it.killedBlazes },
                    Codec.INT.fieldOf("usedSunGrenades").forGetter { it.usedSunGrenades },
                    Codec.BOOL.optionalFieldOf("isNightVisionActive", false).forGetter { it.isNightVisionActive },
                    Codec.BOOL.optionalFieldOf("isSpeedBoostActive", false).forGetter { it.isSpeedBoostActive },
                    Codec.BOOL.optionalFieldOf("isBatFormActive", false).forGetter { it.isBatFormActive },
                    Codec.INT.optionalFieldOf("nightTicker", 0).forGetter { it.nightTicker },
                    Codec.INT.optionalFieldOf("inSunTick", 0).forGetter { it.inSunTick },
                    Codec.INT.optionalFieldOf("maxInSunTickClient", 0).forGetter { it.maxInSunTickClient },
                    Codec.LONG.listOf().fieldOf("visitedVillages").forGetter { it.visitedVillages },
                    UUIDUtil.CODEC.listOf().fieldOf("villagersHalfBlood").forGetter { it.villagersHalfBlood },
                    UUIDUtil.CODEC.listOf().fieldOf("trappedVillagers").forGetter { it.trappedVillagers }
                ).apply(instance, ::VampData)
            }
        }
    }

    data class WereData(
        val killedSheep: Int = 0,
        val killedWolves: Int = 0,
        val killHornedOne: Boolean = false,
        val airSlayMonster: Int = 0,
        val nightHowl: Int = 0,
        val wolfPack: Int = 0,
        val pigmenKilled: Int = 0,
        val spreadLycanthropy: Boolean = false,
        val isWolfManFormActive: Boolean = false,
        val isWolfFormActive: Boolean = false,
        val lycanSourceUUID: Optional<UUID> = Optional.empty(),
        val hasGivenGold: Boolean = false
    ) {
        companion object {
            val CODEC: Codec<WereData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("killedSheep").forGetter { it.killedSheep },
                    Codec.INT.fieldOf("killedWolves").forGetter { it.killedWolves },
                    Codec.BOOL.fieldOf("killHornedOne").forGetter { it.killHornedOne },
                    Codec.INT.fieldOf("airSlayMonster").forGetter { it.airSlayMonster },
                    Codec.INT.fieldOf("nightHowl").forGetter { it.nightHowl },
                    Codec.INT.fieldOf("wolfPack").forGetter { it.wolfPack },
                    Codec.INT.fieldOf("pigmenKilled").forGetter { it.pigmenKilled },
                    Codec.BOOL.fieldOf("spreadLycanthropy").forGetter { it.spreadLycanthropy },
                    Codec.BOOL.fieldOf("isWolfManFormActive").forGetter { it.isWolfManFormActive },
                    Codec.BOOL.fieldOf("isWolfFormActive").forGetter { it.isWolfFormActive },
                    UUIDUtil.CODEC.optionalFieldOf("lycanSourceUUID").forGetter { it.lycanSourceUUID },
                    Codec.BOOL.fieldOf("hasGivenGold").forGetter { it.hasGivenGold }
                ).apply(instance, ::WereData)
            }
        }
    }
}