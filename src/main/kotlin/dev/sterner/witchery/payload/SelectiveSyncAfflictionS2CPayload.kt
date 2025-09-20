package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.StringRepresentable
import java.util.UUID
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import java.util.*


class SelectiveSyncAfflictionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(
        player: Player,
        data: AfflictionPlayerAttachment.Data,
        changedFields: Set<AfflictionPlayerAttachment.SyncField>
    ) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)

        putInt("ChangedFields", changedFields.fold(0) { acc, field ->
            acc or (1 shl field.ordinal)
        })

        val partialData = createPartialData(data, changedFields)

        PartialData.CODEC.encodeStart(NbtOps.INSTANCE, partialData).resultOrPartial().let {
            put("PartialData", it.get())
        }
    })

    constructor(player: Player, data: AfflictionPlayerAttachment.Data) : this(
        player,
        data,
        AfflictionPlayerAttachment.SyncField.entries.toSet() // All fields
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient(payload: SelectiveSyncAfflictionS2CPayload) {
        val client = Minecraft.getInstance()
        val id = payload.nbt.getUUID("Id")

        val player = client.level?.getPlayerByUUID(id)
        if (player == null) return

        client.execute {
            if (payload.nbt.contains("ChangedFields")) {
                handleSelectiveSync(player, payload)
            } else if (payload.nbt.contains("AffData")) {
                handleFullSync(player, payload)
            }
        }
    }

    private fun handleSelectiveSync(player: Player, payload: SelectiveSyncAfflictionS2CPayload) {
        val changedFieldsBits = payload.nbt.getInt("ChangedFields")
        val changedFields = mutableSetOf<AfflictionPlayerAttachment.SyncField>()

        AfflictionPlayerAttachment.SyncField.values().forEach { field ->
            if ((changedFieldsBits and (1 shl field.ordinal)) != 0) {
                changedFields.add(field)
            }
        }

        val partialDataTag = payload.nbt.getCompound("PartialData")
        val partialDataResult = PartialData.CODEC.parse(NbtOps.INSTANCE, partialDataTag).resultOrPartial()

        if (partialDataResult.isPresent) {
            val partialData = partialDataResult.get()
            val currentData = AfflictionPlayerAttachment.getData(player)
            val mergedData = mergePartialData(currentData, partialData, changedFields, player)
            AfflictionPlayerAttachment.setData(player, mergedData, sync = false)
        }
    }

    private fun handleFullSync(player: Player, payload: SelectiveSyncAfflictionS2CPayload) {
        val dataTag = payload.nbt.getCompound("AffData")
        val dataResult = AfflictionPlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        if (dataResult.isPresent) {
            AfflictionPlayerAttachment.setData(player, dataResult.get(), sync = false)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SelectiveSyncAfflictionS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("selective_sync_affliction_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SelectiveSyncAfflictionS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SelectiveSyncAfflictionS2CPayload(buf) }
            )

        private fun createPartialData(
            data: AfflictionPlayerAttachment.Data,
            changedFields: Set<AfflictionPlayerAttachment.SyncField>
        ): PartialData {
            return PartialData(
                afflictionLevels = if (AfflictionPlayerAttachment.SyncField.AFFLICTION_LEVELS in changedFields) {
                    Optional.of(AfflictionTypes.values().mapNotNull { type ->
                        val level = data.getLevel(type)
                        if (level > 0) type to level else null
                    }.toMap())
                } else Optional.empty(),

                abilityIndex = if (AfflictionPlayerAttachment.SyncField.ABILITY_INDEX in changedFields) {
                    Optional.of(data.getAbilityIndex())
                } else Optional.empty(),

                abilityCooldowns = if (AfflictionPlayerAttachment.SyncField.ABILITY_COOLDOWNS in changedFields) {
                    Optional.of(emptyMap())
                } else Optional.empty(),

                vampCombatStats = if (AfflictionPlayerAttachment.SyncField.VAMP_COMBAT_STATS in changedFields) {
                    Optional.of(VampCombatStats(data.getKilledBlazes(), data.getUsedSunGrenades()))
                } else Optional.empty(),

                vampFormStates = if (AfflictionPlayerAttachment.SyncField.VAMP_FORM_STATES in changedFields) {
                    Optional.of(VampFormStates(
                        data.hasNightVision(),
                        data.hasSpeedBoost(),
                        data.isBatForm(),
                        data.getNightTicker(),
                        data.getInSunTick(),
                        data.getMaxInSunTickClient()
                    ))
                } else Optional.empty(),

                vampVillageData = if (AfflictionPlayerAttachment.SyncField.VAMP_VILLAGES in changedFields) {
                    Optional.of(VampVillageData(
                        data.getVisitedVillages(),
                        data.getVillagersHalfBlood(),
                        data.getTrappedVillagers()
                    ))
                } else Optional.empty(),

                wereCombatStats = if (AfflictionPlayerAttachment.SyncField.WERE_COMBAT_STATS in changedFields) {
                    Optional.of(WereCombatStats(
                        data.getKilledSheep(),
                        data.getKilledWolves(),
                        data.hasKilledHornedOne(),
                        data.getAirSlayMonster(),
                        data.getNightHowl(),
                        data.getWolfPack(),
                        data.getPigmenKilled(),
                        data.hasSpreadLycanthropy()
                    ))
                } else Optional.empty(),

                wereFormStates = if (AfflictionPlayerAttachment.SyncField.WERE_FORM_STATES in changedFields) {
                    Optional.of(WereFormStates(
                        data.isWolfManForm(),
                        data.isWolfForm(),
                        data.getLycanSource(),
                        data.hasGivenGold()
                    ))
                } else Optional.empty()
            )
        }

        /**
         * Merge partial data into existing data
         */
        private fun mergePartialData(
            currentData: AfflictionPlayerAttachment.Data,
            partialData: PartialData,
            changedFields: Set<AfflictionPlayerAttachment.SyncField>,
            player: Player
        ): AfflictionPlayerAttachment.Data {
            var mergedData = currentData

            if (AfflictionPlayerAttachment.SyncField.AFFLICTION_LEVELS in changedFields) {
                partialData.afflictionLevels.ifPresent { levels ->
                    AfflictionTypes.values().forEach { type ->
                        mergedData = mergedData.setLevel(type, levels[type] ?: 0)
                    }
                }
            }

            if (AfflictionPlayerAttachment.SyncField.ABILITY_INDEX in changedFields) {
                partialData.abilityIndex.ifPresent { index ->
                    mergedData = mergedData.withAbilityIndex(index)
                }
            }

            if (AfflictionPlayerAttachment.SyncField.ABILITY_COOLDOWNS in changedFields) {
                partialData.abilityCooldowns.ifPresent { cooldowns ->
                    cooldowns.forEach { (ability, cooldown) ->
                        mergedData = mergedData.withAbilityCooldown(ability, cooldown)
                    }
                }
            }

            if (AfflictionPlayerAttachment.SyncField.VAMP_COMBAT_STATS in changedFields) {
                partialData.vampCombatStats.ifPresent { stats ->
                    mergedData = mergedData
                        .withKilledBlazes(stats.killedBlazes)
                        .withUsedSunGrenades(stats.usedSunGrenades)
                }
            }

            if (AfflictionPlayerAttachment.SyncField.VAMP_FORM_STATES in changedFields) {
                partialData.vampFormStates.ifPresent { states ->
                    mergedData = mergedData
                        .withNightVision(states.isNightVisionActive)
                        .withSpeedBoost(states.isSpeedBoostActive)
                        .withBatForm(states.isBatFormActive)
                        .withNightTicker(states.nightTicker)
                        .withInSunTick(states.inSunTick)
                        .withMaxInSunTickClient(states.maxInSunTickClient)
                }
            }

            if (AfflictionPlayerAttachment.SyncField.VAMP_VILLAGES in changedFields) {
                partialData.vampVillageData.ifPresent { villageData ->
                    mergedData = currentData
                    villageData.visitedVillages.forEach { pos ->
                        mergedData = mergedData.addVisitedVillage(pos)
                    }
                    villageData.villagersHalfBlood.forEach { uuid ->
                        mergedData = mergedData.addVillagerHalfBlood(uuid)
                    }
                    villageData.trappedVillagers.forEach { uuid ->
                        mergedData = mergedData.addTrappedVillager(uuid)
                    }
                }
            }

            if (AfflictionPlayerAttachment.SyncField.WERE_COMBAT_STATS in changedFields) {
                partialData.wereCombatStats.ifPresent { stats ->
                    mergedData = mergedData
                        .withKilledSheep(stats.killedSheep)
                        .withKilledWolves(stats.killedWolves)
                        .withKilledHornedOne(stats.killHornedOne)
                        .withAirSlayMonster(stats.airSlayMonster)
                        .withNightHowl(stats.nightHowl)
                        .withWolfPack(stats.wolfPack)
                        .withPigmenKilled(stats.pigmenKilled)
                        .withSpreadLycanthropy(stats.spreadLycanthropy)
                }
            }

            if (AfflictionPlayerAttachment.SyncField.WERE_FORM_STATES in changedFields) {
                partialData.wereFormStates.ifPresent { states ->
                    mergedData = mergedData
                        .withWolfManForm(states.isWolfManFormActive)
                        .withWolfForm(states.isWolfFormActive)
                        .withLycanSource(states.lycanSourceUUID)
                        .withGivenGold(states.hasGivenGold)
                }
            }

            return mergedData
        }
    }

    // =========================
    // PARTIAL DATA CLASSES
    // =========================

    data class PartialData(
        val afflictionLevels: Optional<Map<AfflictionTypes, Int>>,
        val abilityIndex: Optional<Int>,
        val abilityCooldowns: Optional<Map<String, Int>>,
        val vampCombatStats: Optional<VampCombatStats>,
        val vampFormStates: Optional<VampFormStates>,
        val vampVillageData: Optional<VampVillageData>,
        val wereCombatStats: Optional<WereCombatStats>,
        val wereFormStates: Optional<WereFormStates>
    ) {
        companion object {
            val CODEC: Codec<PartialData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(
                        StringRepresentable.fromEnum(AfflictionTypes::values),
                        Codec.INT
                    ).optionalFieldOf("afflictionLevels").forGetter { it.afflictionLevels },
                    Codec.INT.optionalFieldOf("abilityIndex").forGetter { it.abilityIndex },
                    Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("abilityCooldowns").forGetter { it.abilityCooldowns },
                    VampCombatStats.CODEC.optionalFieldOf("vampCombatStats").forGetter { it.vampCombatStats },
                    VampFormStates.CODEC.optionalFieldOf("vampFormStates").forGetter { it.vampFormStates },
                    VampVillageData.CODEC.optionalFieldOf("vampVillageData").forGetter { it.vampVillageData },
                    WereCombatStats.CODEC.optionalFieldOf("wereCombatStats").forGetter { it.wereCombatStats },
                    WereFormStates.CODEC.optionalFieldOf("wereFormStates").forGetter { it.wereFormStates }
                ).apply(instance, ::PartialData)
            }
        }
    }

    data class VampCombatStats(
        val killedBlazes: Int,
        val usedSunGrenades: Int
    ) {
        companion object {
            val CODEC: Codec<VampCombatStats> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("killedBlazes").forGetter { it.killedBlazes },
                    Codec.INT.fieldOf("usedSunGrenades").forGetter { it.usedSunGrenades }
                ).apply(instance, ::VampCombatStats)
            }
        }
    }

    data class VampFormStates(
        val isNightVisionActive: Boolean = false,
        val isSpeedBoostActive: Boolean = false,
        val isBatFormActive: Boolean = false,
        val nightTicker: Int = 0,
        val inSunTick: Int = 0,
        val maxInSunTickClient: Int = 0
    ) {
        companion object {
            val CODEC: Codec<VampFormStates> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.optionalFieldOf("isNightVisionActive", false).forGetter { it.isNightVisionActive },
                    Codec.BOOL.optionalFieldOf("isSpeedBoostActive", false).forGetter { it.isSpeedBoostActive },
                    Codec.BOOL.optionalFieldOf("isBatFormActive", false).forGetter { it.isBatFormActive },
                    Codec.INT.optionalFieldOf("nightTicker", 0).forGetter { it.nightTicker },
                    Codec.INT.optionalFieldOf("inSunTick", 0).forGetter { it.inSunTick },
                    Codec.INT.optionalFieldOf("maxInSunTickClient", 0).forGetter { it.maxInSunTickClient }
                ).apply(instance, ::VampFormStates)
            }
        }
    }

    data class VampVillageData(
        val visitedVillages: List<Long>,
        val villagersHalfBlood: List<UUID>,
        val trappedVillagers: List<UUID>
    ) {
        companion object {
            val CODEC: Codec<VampVillageData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.LONG.listOf().fieldOf("visitedVillages").forGetter { it.visitedVillages },
                    UUIDUtil.CODEC.listOf().fieldOf("villagersHalfBlood").forGetter { it.villagersHalfBlood },
                    UUIDUtil.CODEC.listOf().fieldOf("trappedVillagers").forGetter { it.trappedVillagers }
                ).apply(instance, ::VampVillageData)
            }
        }
    }

    data class WereCombatStats(
        val killedSheep: Int,
        val killedWolves: Int,
        val killHornedOne: Boolean,
        val airSlayMonster: Int,
        val nightHowl: Int,
        val wolfPack: Int,
        val pigmenKilled: Int,
        val spreadLycanthropy: Boolean
    ) {
        companion object {
            val CODEC: Codec<WereCombatStats> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("killedSheep").forGetter { it.killedSheep },
                    Codec.INT.fieldOf("killedWolves").forGetter { it.killedWolves },
                    Codec.BOOL.fieldOf("killHornedOne").forGetter { it.killHornedOne },
                    Codec.INT.fieldOf("airSlayMonster").forGetter { it.airSlayMonster },
                    Codec.INT.fieldOf("nightHowl").forGetter { it.nightHowl },
                    Codec.INT.fieldOf("wolfPack").forGetter { it.wolfPack },
                    Codec.INT.fieldOf("pigmenKilled").forGetter { it.pigmenKilled },
                    Codec.BOOL.fieldOf("spreadLycanthropy").forGetter { it.spreadLycanthropy }
                ).apply(instance, ::WereCombatStats)
            }
        }
    }

    data class WereFormStates(
        val isWolfManFormActive: Boolean,
        val isWolfFormActive: Boolean,
        val lycanSourceUUID: Optional<UUID>,
        val hasGivenGold: Boolean
    ) {
        companion object {
            val CODEC: Codec<WereFormStates> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isWolfManFormActive").forGetter { it.isWolfManFormActive },
                    Codec.BOOL.fieldOf("isWolfFormActive").forGetter { it.isWolfFormActive },
                    UUIDUtil.CODEC.optionalFieldOf("lycanSourceUUID").forGetter { it.lycanSourceUUID },
                    Codec.BOOL.fieldOf("hasGivenGold").forGetter { it.hasGivenGold }
                ).apply(instance, ::WereFormStates)
            }
        }
    }
}
