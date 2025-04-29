package dev.sterner.witchery.handler.vampire

import dev.architectury.event.EventResult
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.event.VampireEvent
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.payload.RefreshDimensionsS2CPayload
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.setData
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos

object VampireLeveling {

    private val KNOCKBACK_BONUS = AttributeModifier(Witchery.id("vampire_knockback"), 0.5, AttributeModifier.Operation.ADD_VALUE)
    private val BAT_WEAKNESS = AttributeModifier(Witchery.id("bat_weakness"), -2.5, AttributeModifier.Operation.ADD_VALUE)
    private val BAT_HEALTH = AttributeModifier(Witchery.id("bat_health"), -4.0, AttributeModifier.Operation.ADD_VALUE)

    @JvmStatic
    fun setLevel(player: ServerPlayer, level: Int) {
        val data = getData(player)
        setData(player, data.copy(vampireLevel = level))
        if (level == 0) {
            VampireAbilityHandler.setAbilityIndex(player, -1)
            TransformationHandler.removeForm(player)
        }
        updateModifiers(player, level, false)
        player.refreshDimensions()
        WitcheryPayloads.sendToPlayers(
            player.level(),
            player.blockPosition(),
            RefreshDimensionsS2CPayload()
        )
    }

    /**
     * Will level upp a vampire-player if they for fills the requirements to do so.
     */
    @JvmStatic
    fun increaseVampireLevel(player: ServerPlayer) {
        val data = getData(player)
        val currentLevel = data.getVampireLevel()
        val nextLevel = currentLevel + 1

        if (nextLevel > 10) return

        if (nextLevel > 2 && !canLevelUp(player, nextLevel)) return

        val result = VampireEvent.ON_LEVEL_UP.invoker().invoke(player, currentLevel, nextLevel)
        if (result == EventResult.interruptFalse()) return

        setLevel(player, nextLevel)
        setMaxBlood(player, nextLevel)
        player.sendSystemMessage(Component.literal("Vampire Level Up: $nextLevel"))
        updateModifiers(player, nextLevel, false)
    }



    /**
     * Maps the current players level to what its max blood pool amount should be
     */
    private fun setMaxBlood(player: Player, level: Int) {
        val maxBlood = levelToBlood(level)
        val bloodData = BloodPoolLivingEntityAttachment.getData(player)
        BloodPoolLivingEntityAttachment.setData(player, bloodData.copy(maxBlood = maxBlood))
    }

    fun levelToBlood(level: Int): Int {
        return when (level) {
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
    }

    /**
     * When the player vampire-level changes this will reset and add potential attributes
     */
    fun updateModifiers(player: Player, level: Int, toBat: Boolean) {
        player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.removeModifier(KNOCKBACK_BONUS)
        player.attributes.getInstance(Attributes.MAX_HEALTH)?.removeModifier(BAT_HEALTH)
        player.attributes.getInstance(Attributes.ARMOR)?.removeModifier(BAT_WEAKNESS)
        if (level >= 3) {
            player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.addPermanentModifier(KNOCKBACK_BONUS)
        }

        if (toBat) {
            player.attributes.getInstance(Attributes.MAX_HEALTH)?.addPermanentModifier(BAT_HEALTH)
            player.attributes.getInstance(Attributes.ARMOR)?.addPermanentModifier(BAT_WEAKNESS)
        }
    }

    /**
     * Checks if the vampire-player is the correct level and have the right amount tof Torn pages to exercise the quest
     */
    fun canPerformQuest(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = getData(player)

        if (data.getVampireLevel() != targetLevel) {
            return false
        }

        val requiredAdvancement = LEVEL_REQUIREMENTS[targetLevel]?.advancement ?: return false
        return TornPageItem.hasAdvancement(player, requiredAdvancement)
    }

    //To go from Level 1 -> 2 is handled in BloodPoolLivingEntityAttachment

    //To go from Level 2 -> 3
    @JvmStatic
    fun increaseVillagersHalfBlood(player: ServerPlayer, villager: Villager) {
        if (!canPerformQuest(player, 2)) {
            return
        }

        val data = getData(player)
        if (!data.villagersHalfBlood.contains(villager.uuid)) {
            val updatedList = data.villagersHalfBlood.toMutableList().apply { add(villager.uuid) }
            setData(player, data.copy(villagersHalfBlood = updatedList))
            increaseVampireLevel(player)
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

    //To go from Level 3 -> 4
    @JvmStatic
    fun increaseNightTicker(player: ServerPlayer) {
        if (!canPerformQuest(player, 3)) {
            return
        }

        val data = getData(player)
        setData(player, data.copy(nightTicker = data.nightTicker + 1), false)

        increaseVampireLevel(player)
    }

    @JvmStatic
    fun resetNightCounter(player: Player) {
        val data = getData(player)
        setData(player, data.copy(nightTicker = 0))
    }

    //To go from Level 4 -> 5
    @JvmStatic
    fun increaseUsedSunGrenades(player: ServerPlayer) {
        if (!canPerformQuest(player, 4)) {
            return
        }

        val data = getData(player)
        setData(player, data.copy(usedSunGrenades = data.usedSunGrenades + 1))

        increaseVampireLevel(player)
    }

    //To go from Level 5 -> 6
    @JvmStatic
    fun increaseKilledBlazes(player: ServerPlayer) {
        if (!canPerformQuest(player, 5)) {
            return
        }

        val data = getData(player)
        setData(player, data.copy(killedBlazes = data.killedBlazes + 1))

        increaseVampireLevel(player)
    }

    //To go from Level 6 -> 7 is handles in LilithEntity
    @JvmStatic
    fun givePoppy(player: ServerPlayer) {
        if (!canPerformQuest(player, 6)) {
            return
        }

        increaseVampireLevel(player)
    }

    //To go from Level 7 -> 8
    @JvmStatic
    fun addVillage(player: ServerPlayer, pos: ChunkPos) {
        if (!canPerformQuest(player, 7)) {
            return
        }

        val data = getData(player)
        val longPos = ChunkPos.asLong(pos.x, pos.z)
        if (!data.visitedVillages.contains(longPos)) {
            val updatedList = data.visitedVillages.toMutableList().apply { add(longPos) }
            setData(player, data.copy(visitedVillages = updatedList))
            increaseVampireLevel(player)
        }
    }

    @JvmStatic
    fun resetVillages(player: Player) {
        val data = getData(player)
        setData(player, data.copy(visitedVillages = mutableListOf()))
    }

    //To go from Level 8 -> 9
    @JvmStatic
    fun increaseTrappedVillagers(player: ServerPlayer, villager: Villager) {
        if (!canPerformQuest(player, 8)) {
            return
        }

        val data = getData(player)
        if (!data.trappedVillagers.contains(villager.uuid)) {
            val updatedList = data.trappedVillagers.toMutableList().apply { add(villager.uuid) }
            setData(player, data.copy(trappedVillagers = updatedList))
            increaseVampireLevel(player)
        }
    }

    @JvmStatic
    fun removeTrappedVillager(player: Player, villager: Villager) {
        val data = getData(player)
        if (data.trappedVillagers.contains(villager.uuid)) {
            val updatedList = data.trappedVillagers.toMutableList().apply { remove(villager.uuid) }
            setData(player, data.copy(trappedVillagers = updatedList))
        }
    }

    val LEVEL_REQUIREMENTS: Map<Int, Requirement> = mapOf(
        3 to Requirement(Witchery.id("vampire/2"), halfVillagers = 5),
        4 to Requirement(Witchery.id("vampire/3"), nightCounter = 24000),
        5 to Requirement(Witchery.id("vampire/4"), sunGrenades = 10),
        6 to Requirement(Witchery.id("vampire/5"), blazesKilled = 20),
        7 to Requirement(Witchery.id("vampire/6")),
        8 to Requirement(Witchery.id("vampire/7"), villagesVisited = 2),
        9 to Requirement(Witchery.id("vampire/8"), trappedVillagers = 5),
        10 to Requirement(Witchery.id("vampire/9"))
    )

    private fun canLevelUp(player: ServerPlayer, targetLevel: Int): Boolean {
        if (targetLevel > 10) {
            return false
        }

        if (targetLevel <= 2) {
            return true
        }

        val data = getData(player)
        val requirement = LEVEL_REQUIREMENTS[targetLevel] ?: return false

        return (requirement.advancement.let { TornPageItem.hasAdvancement(player, it) } &&
                (requirement.halfVillagers?.let { data.villagersHalfBlood.size >= it } ?: true) &&
                (requirement.nightCounter?.let { data.nightTicker >= it && player.level().isNight } ?: true) &&
                (requirement.sunGrenades?.let { data.usedSunGrenades >= it } ?: true) &&
                (requirement.blazesKilled?.let { data.killedBlazes >= it } ?: true) &&
                (requirement.villagesVisited?.let { data.visitedVillages.size >= it } ?: true) &&
                (requirement.trappedVillagers?.let { data.trappedVillagers.size >= it } ?: true)
                )
    }


    data class Requirement(
        val advancement: ResourceLocation,
        val halfVillagers: Int? = null,
        val nightCounter: Int? = null,
        val sunGrenades: Int? = null,
        val blazesKilled: Int? = null,
        val villagesVisited: Int? = null,
        val trappedVillagers: Int? = null
    )
}