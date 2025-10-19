package dev.sterner.witchery.core.data_attachment.affliction

import com.mojang.serialization.Codec
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import net.minecraft.core.UUIDUtil
import net.minecraft.util.StringRepresentable

object SyncFieldRegistry {

    val fields = listOf(
        // ===== CORE AFFLICTION DATA =====
        FieldDefinition(
            "afflictionLevels",
            Codec.unboundedMap(StringRepresentable.fromEnum(AfflictionTypes::values), Codec.INT),
            { data -> data.afflictionLevels.toMap() },
            { data, value ->
                value.entries.fold(data) { acc, (type, level) ->
                    acc.setLevel(type, level)
                }
            }
        ),

        FieldDefinition(
            "abilityIndex",
            Codec.INT,
            { it.getAbilityIndex() },
            { data, value -> data.withAbilityIndex(value) }
        ),

        FieldDefinition(
            "abilityCooldowns",
            Codec.unboundedMap(Codec.STRING, Codec.INT),
            { data -> data.abilityCooldowns.toMap() },
            { data, value ->
                value.entries.fold(data) { acc, (ability, cooldown) ->
                    acc.withAbilityCooldown(ability, cooldown)
                }
            }
        ),

        FieldDefinition(
            "selectedAbilities",
            Codec.STRING.listOf(),
            { it.getSelectedAbilities() },
            { data, value -> data.withSelectedAbilities(value) }
        ),

        // ===== VAMPIRE DATA =====
        FieldDefinition(
            "vampData.killedBlazes",
            Codec.INT,
            { it.vampData.killedBlazes },
            { data, value -> data.withKilledBlazes(value) }
        ),

        FieldDefinition(
            "vampData.usedSunGrenades",
            Codec.INT,
            { it.vampData.usedSunGrenades },
            { data, value -> data.withUsedSunGrenades(value) }
        ),

        FieldDefinition(
            "vampData.nightTicker",
            Codec.INT,
            { it.vampData.nightTicker },
            { data, value -> data.withNightTicker(value) }
        ),

        FieldDefinition(
            "vampData.inSunTick",
            Codec.INT,
            { it.vampData.inSunTick },
            { data, value -> data.withInSunTick(value) }
        ),

        FieldDefinition(
            "vampData.maxInSunTickClient",
            Codec.INT,
            { it.vampData.maxInSunTickClient },
            { data, value -> data.withMaxInSunTickClient(value) }
        ),

        FieldDefinition(
            "vampData.isNightVisionActive",
            Codec.BOOL,
            { it.vampData.isNightVisionActive },
            { data, value -> data.withNightVision(value) }
        ),

        FieldDefinition(
            "vampData.isSpeedBoostActive",
            Codec.BOOL,
            { it.vampData.isSpeedBoostActive },
            { data, value -> data.withSpeedBoost(value) }
        ),

        FieldDefinition(
            "vampData.isBatFormActive",
            Codec.BOOL,
            { it.vampData.isBatFormActive },
            { data, value -> data.withBatForm(value) }
        ),

        FieldDefinition(
            "vampData.visitedVillages",
            Codec.LONG.listOf(),
            { it.vampData.visitedVillages },
            { data, value ->
                data.clearVisitedVillages().let { clearedData ->
                    value.fold(clearedData) { acc, pos -> acc.addVisitedVillage(pos) }
                }
            }
        ),

        FieldDefinition(
            "vampData.villagersHalfBlood",
            UUIDUtil.CODEC.listOf(),
            { it.vampData.villagersHalfBlood },
            { data, value ->
                data.clearVillagerHalfBlood().let { clearedData ->
                    value.fold(clearedData) { acc, uuid -> acc.addVillagerHalfBlood(uuid) }
                }
            }
        ),

        FieldDefinition(
            "vampData.trappedVillagers",
            UUIDUtil.CODEC.listOf(),
            { it.vampData.trappedVillagers },
            { data, value ->
                data.clearTrappedVillager().let { clearedData ->
                    value.fold(clearedData) { acc, uuid -> acc.addTrappedVillager(uuid) }
                }
            }
        ),

        // ===== WEREWOLF DATA =====
        FieldDefinition(
            "wereData.killedSheep",
            Codec.INT,
            { it.wereData.killedSheep },
            { data, value -> data.withKilledSheep(value) }
        ),

        FieldDefinition(
            "wereData.killedWolves",
            Codec.INT,
            { it.wereData.killedWolves },
            { data, value -> data.withKilledWolves(value) }
        ),

        FieldDefinition(
            "wereData.killHornedOne",
            Codec.BOOL,
            { it.wereData.killHornedOne },
            { data, value -> data.withKilledHornedOne(value) }
        ),

        FieldDefinition(
            "wereData.airSlayMonster",
            Codec.INT,
            { it.wereData.airSlayMonster },
            { data, value -> data.withAirSlayMonster(value) }
        ),

        FieldDefinition(
            "wereData.nightHowl",
            Codec.INT,
            { it.wereData.nightHowl },
            { data, value -> data.withNightHowl(value) }
        ),

        FieldDefinition(
            "wereData.wolfPack",
            Codec.INT,
            { it.wereData.wolfPack },
            { data, value -> data.withWolfPack(value) }
        ),

        FieldDefinition(
            "wereData.pigmenKilled",
            Codec.INT,
            { it.wereData.pigmenKilled },
            { data, value -> data.withPigmenKilled(value) }
        ),

        FieldDefinition(
            "wereData.spreadLycanthropy",
            Codec.BOOL,
            { it.wereData.spreadLycanthropy },
            { data, value -> data.withSpreadLycanthropy(value) }
        ),

        FieldDefinition(
            "wereData.isWolfManFormActive",
            Codec.BOOL,
            { it.wereData.isWolfManFormActive },
            { data, value -> data.withWolfManForm(value) }
        ),

        FieldDefinition(
            "wereData.isWolfFormActive",
            Codec.BOOL,
            { it.wereData.isWolfFormActive },
            { data, value -> data.withWolfForm(value) }
        ),

        FieldDefinition(
            "wereData.lycanSourceUUID",
            UUIDUtil.CODEC.optionalFieldOf("lycanSource").codec(),
            { it.wereData.lycanSourceUUID },
            { data, value -> data.withLycanSource(value) }
        ),

        FieldDefinition(
            "wereData.hasGivenGold",
            Codec.BOOL,
            { it.wereData.hasGivenGold },
            { data, value -> data.withGivenGold(value) }
        ),

        // ===== LICH DATA =====
        FieldDefinition(
            "lichData.readTablets",
            UUIDUtil.CODEC.listOf(),
            { it.lichData.readTablets },
            { data, value -> data.withReadTablets(value) }
        ),

        FieldDefinition(
            "lichData.boundSouls",
            Codec.INT,
            { it.lichData.boundSouls },
            { data, value -> data.withBoundSouls(value) }
        ),

        FieldDefinition(
            "lichData.zombieKilledMob",
            Codec.BOOL,
            { it.lichData.zombieKilledMob },
            { data, value -> data.withZombieKilledMob(value) }
        ),

        FieldDefinition(
            "lichData.killedGolems",
            Codec.INT,
            { it.lichData.killedGolems },
            { data, value -> data.withGolemKills(value) }
        ),

        FieldDefinition(
            "lichData.drainedAnimals",
            Codec.INT,
            { it.lichData.drainedAnimals },
            { data, value -> data.withDrainedAnimals(value) }
        ),

        FieldDefinition(
            "lichData.possessedKillVillager",
            Codec.BOOL,
            { it.lichData.possessedKillVillager },
            { data, value -> data.withPossessedKillVillager(value) }
        ),

        FieldDefinition(
            "lichData.killedWither",
            Codec.BOOL,
            { it.lichData.killedWither },
            { data, value -> data.withKilledWither(value) }
        ),

        FieldDefinition(
            "lichData.phylacteryBound",
            Codec.BOOL,
            { it.lichData.phylacteryBound },
            { data, value -> data.withPhylacteryBound(value) }
        ),

        FieldDefinition(
            "lichData.phylacteryDeaths",
            Codec.INT,
            { it.lichData.phylacteryDeaths },
            { data, value -> data.withPhylacteryDeaths(value) }
        ),

        FieldDefinition(
            "lichData.phylacteryDeathTimes",
            Codec.LONG.listOf(),
            { it.lichData.phylacteryDeathTimes },
            { data, value -> data.withPhylacteryDeathTimes(value) }
        ),

        FieldDefinition(
            "lichData.phylacterySouls",
            Codec.INT,
            { it.lichData.phylacterySouls },
            { data, value -> data.withPhylacterySouls(value) }
        ),

        FieldDefinition(
            "lichData.isSoulFormActive",
            Codec.BOOL,
            { it.lichData.isSoulFormActive },
            { data, value -> data.withSoulForm(value) }
        ),

        FieldDefinition(
            "lichData.isVagrant",
            Codec.BOOL,
            { it.lichData.isVagrant },
            { data, value -> data.withVagrant(value) }
        )
    )

    val fieldsByPath = fields.associateBy { it.path }
}
