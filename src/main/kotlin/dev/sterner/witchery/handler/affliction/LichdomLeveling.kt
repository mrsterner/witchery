package dev.sterner.witchery.handler.affliction

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.event.LichEvent
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.common.NeoForge
import java.util.*

object LichdomLeveling {

    private val SOUL_DRAIN_WEAKNESS =
        AttributeModifier(Witchery.id("lich_soul_drain"), -2.0, AttributeModifier.Operation.ADD_VALUE)
    private val LICH_HEALTH_BONUS =
        AttributeModifier(Witchery.id("lich_health"), 10.0, AttributeModifier.Operation.ADD_VALUE)
    private val LICH_ARMOR_BONUS =
        AttributeModifier(Witchery.id("lich_armor"), 4.0, AttributeModifier.Operation.ADD_VALUE)

    @JvmStatic
    fun setLevel(player: ServerPlayer, level: Int) {
        AfflictionPlayerAttachment.batchUpdate(player) {
            var result = setLevel(AfflictionTypes.LICHDOM, level)

            if (level == 0) {
                result = result
                    .withSoulForm(false)
                    .withPhylacteryBound(false)
                    .withPhylacterySouls(0)
            }

            result
        }

        if (level >= 2) {
            LichdomSoulPoolHandler.setMaxSouls(player, level)
        }

        if (level == 0) {
            TransformationHandler.removeForm(player)
        }

        updateModifiers(player, level)
        player.refreshDimensions()
    }

    @JvmStatic
    fun increaseNecromancerLevel(player: ServerPlayer) {
        val currentData = AfflictionPlayerAttachment.getData(player)
        val currentLevel = currentData.getLevel(AfflictionTypes.LICHDOM)
        val nextLevel = currentLevel + 1

        if (nextLevel > 10) return

        if (nextLevel > 1 && !canLevelUp(player, currentData, nextLevel)) return

        val event = LichEvent.LevelUp(player, currentLevel, nextLevel)
        NeoForge.EVENT_BUS.post(event)
        if (event.isCanceled) {
            return
        }

        setLevel(player, nextLevel)
        player.sendSystemMessage(Component.literal("Necromancer Level Up: $nextLevel"))
    }

    private fun canPerformQuest(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = AfflictionPlayerAttachment.getData(player)

        if (data.getLevel(AfflictionTypes.LICHDOM) != targetLevel) {
            return false
        }

        val requiredTablet = LEVEL_REQUIREMENTS[targetLevel]?.tabletTier ?: return true
        return hasTabletKnowledge(player, requiredTablet)
    }

    private fun hasTabletKnowledge(player: ServerPlayer, tier: Int): Boolean {
        val data = AfflictionPlayerAttachment.getData(player)
        return data.getReadTablets().size >= tier
    }

    // Level 1 -> 2: Bind villager soul
    @JvmStatic
    fun bindVillagerSoul(player: ServerPlayer) {
        if (!canPerformQuest(player, 1)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            incrementBoundSouls()
        }

        checkAndLevelUp(player, newData)
    }

    // Level 2 -> 3: Zombie kills mob
    @JvmStatic
    fun recordZombieKill(player: ServerPlayer) {
        if (!canPerformQuest(player, 2)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            withZombieKilledMob(true)
        }

        checkAndLevelUp(player, newData)
    }

    // Level 3 -> 4: Kill golems
    @JvmStatic
    fun increaseKilledGolems(player: ServerPlayer) {
        if (!canPerformQuest(player, 3)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            incrementKilledGolems()
        }

        checkAndLevelUp(player, newData)
    }

    // Level 4 -> 5: Bind 3 villager souls
    @JvmStatic
    fun bindMultipleSouls(player: ServerPlayer, count: Int) {
        if (!canPerformQuest(player, 4)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            withBoundSouls(getBoundSouls() + count)
        }

        checkAndLevelUp(player, newData)
    }

    // Level 5 -> 6: Drain animals
    @JvmStatic
    fun drainAnimalLife(player: ServerPlayer) {
        if (!canPerformQuest(player, 5)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            incrementDrainedAnimals()
        }

        checkAndLevelUp(player, newData)
    }

    // Level 6 -> 7: Possess and kill
    @JvmStatic
    fun recordPossessedKill(player: ServerPlayer) {
        if (!canPerformQuest(player, 6)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            withPossessedKillVillager(true)
        }

        checkAndLevelUp(player, newData)
    }

    // Level 7 -> 8: Kill wither
    @JvmStatic
    fun recordWitherKill(player: ServerPlayer) {
        if (!canPerformQuest(player, 7)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            withKilledWither(true)
        }

        checkAndLevelUp(player, newData)
    }

    // Level 8 -> 9: Phylactery save
    @JvmStatic
    fun recordPhylacteryUse(player: ServerPlayer) {
        if (!canPerformQuest(player, 8)) return

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            withPhylacteryBound(true).incrementPhylacteryDeaths(player)
        }

        checkAndLevelUp(player, newData)
    }

    // Level 9 -> 10: Triple death
    @JvmStatic
    fun recordPhylacteryTripleDeath(player: ServerPlayer) {
        if (!canPerformQuest(player, 9)) return

        val data = AfflictionPlayerAttachment.getData(player)
        val recentDeaths = data.getPhylacteryDeaths()

        if (recentDeaths >= 3) {
            increaseNecromancerLevel(player)
        }
    }

    // Tablet reading
    @JvmStatic
    fun readAncientTablet(player: ServerPlayer, tabletId: UUID): Boolean {
        val data = AfflictionPlayerAttachment.getData(player)

        if (data.getReadTablets().contains(tabletId)) {
            player.sendSystemMessage(Component.literal("You have already studied this tablet"))
            return false
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            addReadTablet(tabletId)
        }

        val tabletsRead = newData.getReadTablets().size
        player.sendSystemMessage(Component.literal("Ancient knowledge gained... ($tabletsRead/3 tablets read)"))

        return true
    }

    fun updateModifiers(player: Player, level: Int) {
        player.attributes.getInstance(Attributes.MAX_HEALTH)?.removeModifier(SOUL_DRAIN_WEAKNESS)
        player.attributes.getInstance(Attributes.MAX_HEALTH)?.removeModifier(LICH_HEALTH_BONUS)
        player.attributes.getInstance(Attributes.ARMOR)?.removeModifier(LICH_ARMOR_BONUS)

        if (level >= 3) {
            player.attributes.getInstance(Attributes.MAX_HEALTH)?.addPermanentModifier(SOUL_DRAIN_WEAKNESS)
        }

        if (level >= 10) {
            player.attributes.getInstance(Attributes.MAX_HEALTH)?.addPermanentModifier(LICH_HEALTH_BONUS)
            player.attributes.getInstance(Attributes.ARMOR)?.addPermanentModifier(LICH_ARMOR_BONUS)
        }
    }

    private fun checkAndLevelUp(player: ServerPlayer, data: AfflictionPlayerAttachment.Data) {
        val currentLevel = data.getLevel(AfflictionTypes.LICHDOM)
        val nextLevel = currentLevel + 1

        if (nextLevel <= 10 && canLevelUp(player, data, nextLevel)) {
            increaseNecromancerLevel(player)
        }
    }

    val LEVEL_REQUIREMENTS: Map<Int, Requirement> = mapOf(
        2 to Requirement(tabletTier = 1, boundSouls = 1),
        3 to Requirement(tabletTier = 1, zombieKilledMob = true),
        4 to Requirement(tabletTier = 1, killedGolems = 5),
        5 to Requirement(tabletTier = 1, boundSouls = 3),
        6 to Requirement(tabletTier = 2, drainedAnimals = 5),
        7 to Requirement(tabletTier = 2, possessedKillVillager = true),
        8 to Requirement(tabletTier = 2, killedWither = true),
        9 to Requirement(tabletTier = 3, phylacteryBound = true, phylacteryDeaths = 1),
        10 to Requirement(tabletTier = 3, phylacteryDeathsInHour = 3)
    )

    private fun canLevelUp(player: ServerPlayer, data: AfflictionPlayerAttachment.Data, targetLevel: Int): Boolean {
        if (targetLevel > 10) return false
        if (targetLevel == 1) return true

        val requirement = LEVEL_REQUIREMENTS[targetLevel] ?: return false

        return (requirement.tabletTier?.let { hasTabletKnowledge(player, it) } ?: true) &&
                (requirement.boundSouls?.let { data.getBoundSouls() >= it } ?: true) &&
                (requirement.zombieKilledMob?.let { data.hasZombieKilledMob() } ?: true) &&
                (requirement.killedGolems?.let { data.getKilledGolems() >= it } ?: true) &&
                (requirement.drainedAnimals?.let { data.getDrainedAnimals() >= it } ?: true) &&
                (requirement.possessedKillVillager?.let { data.hasPossessedKillVillager() } ?: true) &&
                (requirement.killedWither?.let { data.hasKilledWither() } ?: true) &&
                (requirement.phylacteryBound?.let { data.isPhylacteryBound() } ?: true) &&
                (requirement.phylacteryDeaths?.let { data.getPhylacteryDeaths() >= it } ?: true) &&
                (requirement.phylacteryDeathsInHour?.let { data.getPhylacteryDeaths() >= it } ?: true)
    }

    data class Requirement(
        val tabletTier: Int? = null,
        val boundSouls: Int? = null,
        val zombieKilledMob: Boolean? = null,
        val killedGolems: Int? = null,
        val drainedAnimals: Int? = null,
        val possessedKillVillager: Boolean? = null,
        val killedWither: Boolean? = null,
        val phylacteryBound: Boolean? = null,
        val phylacteryDeaths: Int? = null,
        val phylacteryDeathsInHour: Int? = null
    )

    //Level 1 is auto from necro infusion
    //Everything below is locked behind found ancient stone carving about lichdoom and the soul cage.
    //level 2 is binding one villager soul to soul cage
    //level 3 is letting a zombie slave kill a mob
    //level 4 is killing 5 iron golems or snow golems
    //level 5 is binding 3 villagers souls to a soul cage
    //Everything below is locked by finding another stone carving about lichdom and the severed soul
    //level 6 is draining life force from 5 animals to death
    //level 7 separate your soul from your body and possess a zombie and kill a villager
    //level 8 kill a wither
    //Everything below is locked behind a stone carving about the phylactery.
    //level 9 bind your soul to a phylactery and let it save you
    //level 10


    //abilities?
    /*
    corpse explosion
    summon zombies
    teleport to last death location
    hold entity in magic circle
    increase summonable count
    phylactery level 1 (one soul)
    phylactery level 3 (three souls)
    immunity to one hit kill
    separate soul from body
    posses other mobs when separated from own body
    life drain

    //drawbacks
    no hunger, only soul
    cant heal without life drain
    crops dies when walk on
    animals flee
    cannot wear gold armor
    positive potions turn negative
    phylactery needs skylight
     */
}