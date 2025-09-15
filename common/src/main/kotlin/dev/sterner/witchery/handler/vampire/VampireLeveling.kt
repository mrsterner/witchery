package dev.sterner.witchery.handler.vampire

import dev.architectury.event.EventResult
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.event.VampireEvent
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.payload.RefreshDimensionsS2CPayload
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
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

    private val KNOCKBACK_BONUS =
        AttributeModifier(Witchery.id("vampire_knockback"), 0.5, AttributeModifier.Operation.ADD_VALUE)
    private val BAT_WEAKNESS =
        AttributeModifier(Witchery.id("bat_weakness"), -2.5, AttributeModifier.Operation.ADD_VALUE)
    private val BAT_HEALTH = AttributeModifier(Witchery.id("bat_health"), -4.0, AttributeModifier.Operation.ADD_VALUE)

    @JvmStatic
    fun setLevel(player: ServerPlayer, level: Int) {
        AfflictionPlayerAttachment.batchUpdate(player) {
            var result = setLevel(AfflictionTypes.VAMPIRE, level)

            if (level == 0) {
                result = result
                    .withAbilityIndex(-1)
                    .withNightVision(false)
                    .withSpeedBoost(false)
                    .withBatForm(false)
            }

            result
        }

        if (level == 0) {
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
     * Will level up a vampire-player if they fulfill the requirements to do so.
     */
    @JvmStatic
    fun increaseVampireLevel(player: ServerPlayer) {
        val currentData = AfflictionPlayerAttachment.getData(player)
        val currentLevel = currentData.getLevel(AfflictionTypes.VAMPIRE)
        val nextLevel = currentLevel + 1

        if (nextLevel > 10) return

        if (nextLevel > 2 && !canLevelUp(player, currentData, nextLevel)) return

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
     * Checks if the vampire-player is the correct level and has the right amount of Torn pages to exercise the quest
     */
    fun canPerformQuest(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = AfflictionPlayerAttachment.getData(player)

        if (data.getLevel(AfflictionTypes.VAMPIRE) != targetLevel) {
            return false
        }

        val requiredAdvancement = LEVEL_REQUIREMENTS[targetLevel]?.advancement ?: return false
        return TornPageItem.hasAdvancement(player, requiredAdvancement)
    }

    // ==========================================
    // LEVEL 2 -> 3: Half-blood villagers
    // ==========================================

    @JvmStatic
    fun increaseVillagersHalfBlood(player: ServerPlayer, villager: Villager) {
        if (!canPerformQuest(player, 2)) {
            return
        }

        val data = AfflictionPlayerAttachment.getData(player)
        if (!data.getVillagersHalfBlood().contains(villager.uuid)) {
            AfflictionPlayerAttachment.batchUpdate(player) {
                addVillagerHalfBlood(villager.uuid)
            }
            checkAndLevelUp(player, data.addVillagerHalfBlood(villager.uuid))
        }
    }

    @JvmStatic
    fun removeVillagerHalfBlood(player: Player, villager: Villager) {
        val data = AfflictionPlayerAttachment.getData(player)
        if (data.getVillagersHalfBlood().contains(villager.uuid)) {
            AfflictionPlayerAttachment.batchUpdate(player) {
                removeVillagerHalfBlood(villager.uuid)
            }
        }
    }

    // ==========================================
    // LEVEL 3 -> 4: Night counter
    // ==========================================

    @JvmStatic
    fun increaseNightTicker(player: ServerPlayer) {
        if (!canPerformQuest(player, 3)) {
            return
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player, sync = false) {
            incrementNightTicker()
        }

        checkAndLevelUp(player, newData)
    }

    @JvmStatic
    fun resetNightCounter(player: Player) {
        AfflictionPlayerAttachment.batchUpdate(player, sync = false) {
            withNightTicker(0)
        }
    }

    // ==========================================
    // LEVEL 4 -> 5: Sun grenades
    // ==========================================

    @JvmStatic
    fun increaseUsedSunGrenades(player: ServerPlayer) {
        if (!canPerformQuest(player, 4)) {
            return
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            incrementUsedSunGrenades()
        }

        checkAndLevelUp(player, newData)
    }

    // ==========================================
    // LEVEL 5 -> 6: Killed blazes
    // ==========================================

    @JvmStatic
    fun increaseKilledBlazes(player: ServerPlayer) {
        if (!canPerformQuest(player, 5)) {
            return
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player) {
            incrementKilledBlazes()
        }

        checkAndLevelUp(player, newData)
    }

    // ==========================================
    // LEVEL 6 -> 7: Give poppy (handled in LilithEntity)
    // ==========================================

    @JvmStatic
    fun givePoppy(player: ServerPlayer) {
        if (!canPerformQuest(player, 6)) {
            return
        }

        increaseVampireLevel(player)
    }

    // ==========================================
    // LEVEL 7 -> 8: Visit villages
    // ==========================================

    @JvmStatic
    fun addVillage(player: ServerPlayer, pos: ChunkPos) {
        if (!canPerformQuest(player, 7)) {
            return
        }

        val data = AfflictionPlayerAttachment.getData(player)
        val longPos = ChunkPos.asLong(pos.x, pos.z)

        if (!data.getVisitedVillages().contains(longPos)) {
            val newData = AfflictionPlayerAttachment.batchUpdate(player) {
                addVisitedVillage(longPos)
            }
            checkAndLevelUp(player, newData)
        }
    }

    @JvmStatic
    fun resetVillages(player: Player) {
        val data = AfflictionPlayerAttachment.getData(player)
        AfflictionPlayerAttachment.batchUpdate(player) {
            var result = this
            data.getVisitedVillages().forEach { village ->
                result = result.removeVisitedVillage(village)
            }
            result
        }
    }

    // ==========================================
    // LEVEL 8 -> 9: Trap villagers
    // ==========================================

    @JvmStatic
    fun increaseTrappedVillagers(player: ServerPlayer, villager: Villager) {
        if (!canPerformQuest(player, 8)) {
            return
        }

        val data = AfflictionPlayerAttachment.getData(player)
        if (!data.getTrappedVillagers().contains(villager.uuid)) {
            val newData = AfflictionPlayerAttachment.batchUpdate(player) {
                addTrappedVillager(villager.uuid)
            }
            checkAndLevelUp(player, newData)
        }
    }

    @JvmStatic
    fun removeTrappedVillager(player: Player, villager: Villager) {
        val data = AfflictionPlayerAttachment.getData(player)
        if (data.getTrappedVillagers().contains(villager.uuid)) {
            AfflictionPlayerAttachment.batchUpdate(player) {
                removeTrappedVillager(villager.uuid)
            }
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Check if requirements are met and level up if so
     */
    private fun checkAndLevelUp(player: ServerPlayer, data: AfflictionPlayerAttachment.Data) {
        val currentLevel = data.getLevel(AfflictionTypes.VAMPIRE)
        val nextLevel = currentLevel + 1

        if (nextLevel <= 10 && canLevelUp(player, data, nextLevel)) {
            increaseVampireLevel(player)
        }
    }

    /**
     * Bulk update for debugging/admin commands
     */
    @JvmStatic
    fun setVampireProgress(
        player: ServerPlayer,
        level: Int? = null,
        killedBlazes: Int? = null,
        usedSunGrenades: Int? = null,
        nightTicker: Int? = null
    ) {
        AfflictionPlayerAttachment.batchUpdate(player) {
            var result = this

            level?.let {
                result = result.setLevel(AfflictionTypes.VAMPIRE, it)
            }
            killedBlazes?.let {
                result = result.withKilledBlazes(it)
            }
            usedSunGrenades?.let {
                result = result.withUsedSunGrenades(it)
            }
            nightTicker?.let {
                result = result.withNightTicker(it)
            }

            result
        }
    }

    /**
     * Reset all vampire progress (for debugging or penalties)
     */
    @JvmStatic
    fun resetVampireProgress(player: ServerPlayer, keepLevel: Boolean = false) {
        AfflictionPlayerAttachment.batchUpdate(player) {
            var result = this

            if (!keepLevel) {
                result = result.setLevel(AfflictionTypes.VAMPIRE, 0)
            }

            result
                .withKilledBlazes(0)
                .withUsedSunGrenades(0)
                .withNightTicker(0)
                .withNightVision(false)
                .withSpeedBoost(false)
                .withBatForm(false)
                .withAbilityIndex(-1)
                .withInSunTick(0)
                .withMaxInSunTickClient(0)
                .clearKilledBlazes()
                .clearTrappedVillager()
                .clearVisitedVillages()
                .clearUsedSunGrenades()
                .clearVillagerHalfBlood()
        }
    }

    // ==========================================
    // REQUIREMENTS
    // ==========================================

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

    private fun canLevelUp(player: ServerPlayer, data: AfflictionPlayerAttachment.Data, targetLevel: Int): Boolean {
        if (targetLevel > 10) {
            return false
        }

        if (targetLevel <= 2) {
            return true
        }

        val requirement = LEVEL_REQUIREMENTS[targetLevel] ?: return false

        return (requirement.advancement.let { TornPageItem.hasAdvancement(player, it) } &&
                (requirement.halfVillagers?.let { data.getVillagersHalfBlood().size >= it } ?: true) &&
                (requirement.nightCounter?.let { data.getNightTicker() >= it && player.level().isNight } ?: true) &&
                (requirement.sunGrenades?.let { data.getUsedSunGrenades() >= it } ?: true) &&
                (requirement.blazesKilled?.let { data.getKilledBlazes() >= it } ?: true) &&
                (requirement.villagesVisited?.let { data.getVisitedVillages().size >= it } ?: true) &&
                (requirement.trappedVillagers?.let { data.getTrappedVillagers().size >= it } ?: true)
                )
    }

    /**
     * Optimized level checking for batch operations
     */
    fun canLevelUp(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = AfflictionPlayerAttachment.getData(player)
        return canLevelUp(player, data, targetLevel)
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