package dev.sterner.witchery.handler.vampire

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object VampireLevelRequirements {

    val LEVEL_REQUIREMENTS: Map<Int, LevelRequirement> = mapOf(
        3 to LevelRequirement(Witchery.id("vampire/2"), halfVillagers = 5),
        4 to LevelRequirement(Witchery.id("vampire/3"), nightCounter = 24000),
        5 to LevelRequirement(Witchery.id("vampire/4"), sunGrenades = 10),
        6 to LevelRequirement(Witchery.id("vampire/5"), blazesKilled = 20),
        7 to LevelRequirement(Witchery.id("vampire/6")),
        8 to LevelRequirement(Witchery.id("vampire/7"), villagesVisited = 2),
        9 to LevelRequirement(Witchery.id("vampire/8"), trappedVillagers = 5)
    )

    fun canLevelUp(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = VampirePlayerAttachment.getData(player)
        val requirement = LEVEL_REQUIREMENTS[targetLevel] ?: return false

        return (requirement.advancement.let { TornPageItem.hasAdvancement(player, it) } &&
                (requirement.halfVillagers?.let { data.villagersHalfBlood.size >= it } ?: true) &&
                (requirement.nightCounter?.let { data.nightTicker >= it } ?: true) &&
                (requirement.sunGrenades?.let { data.usedSunGrenades >= it } ?: true) &&
                (requirement.blazesKilled?.let { data.killedBlazes >= it } ?: true) &&
                (requirement.villagesVisited?.let { data.visitedVillages.size >= it } ?: true) &&
                (requirement.trappedVillagers?.let { data.trappedVillagers >= it } ?: true)
                )
    }

    data class LevelRequirement(
        val advancement: ResourceLocation,
        val halfVillagers: Int? = null,
        val nightCounter: Int? = null,
        val sunGrenades: Int? = null,
        val blazesKilled: Int? = null,
        val villagesVisited: Int? = null,
        val trappedVillagers: Int? = null
    )
}

