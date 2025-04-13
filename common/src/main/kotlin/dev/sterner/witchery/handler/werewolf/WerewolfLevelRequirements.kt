package dev.sterner.witchery.handler.werewolf

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object WerewolfLevelRequirements {

    val LEVEL_REQUIREMENTS: Map<Int, LevelRequirement> = mapOf(
        2 to LevelRequirement(Witchery.id("werewolf/1"), threeGold = true),
        3 to LevelRequirement(Witchery.id("werewolf/2"), killedSheep = 30),
        4 to LevelRequirement(Witchery.id("werewolf/3"), killedWolves = 10),
        5 to LevelRequirement(Witchery.id("werewolf/4"), killHornedOne = true),
        6 to LevelRequirement(Witchery.id("werewolf/5"), airSlayMonster = 10),
        7 to LevelRequirement(Witchery.id("werewolf/6"), nightHowl = 10),
        8 to LevelRequirement(Witchery.id("werewolf/7"), wolfPack = 6),
        9 to LevelRequirement(Witchery.id("werewolf/8"), pigmenKilled = 30),
        10 to LevelRequirement(Witchery.id("werewolf/9"), spreadLycantropy = true)
    )

    fun canLevelUp(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = WerewolfPlayerAttachment.getData(player)
        val requirement = LEVEL_REQUIREMENTS[targetLevel] ?: return false

        return ((requirement.threeGold?.let { data.hasGivenGold == it } ?: true) &&
                (requirement.killedSheep?.let { data.killedSheep >= it } ?: true) &&
                (requirement.killedWolves?.let { data.killedWolves >= it } ?: true) &&
                (requirement.killHornedOne?.let { data.killHornedOne == it } ?: true) &&
                (requirement.airSlayMonster?.let { data.airSlayMonster == it } ?: true) &&
                (requirement.nightHowl?.let { data.nightHowl == it } ?: true) &&
                (requirement.wolfPack?.let { data.wolfPack == it } ?: true) &&
                (requirement.pigmenKilled?.let { data.pigmenKilled == it } ?: true) &&
                (requirement.spreadLycantropy?.let { data.spreadLycantropy == it } ?: true)
                )
    }

    data class LevelRequirement(
        val advancement: ResourceLocation,
        val threeGold: Boolean? = null,
        val killedSheep: Int? = null,
        val killedWolves: Int? = null,
        val killHornedOne: Boolean? = null,
        val airSlayMonster: Int? = null,
        val nightHowl: Int? = null,
        val wolfPack: Int? = null,
        val pigmenKilled: Int? = null,
        val spreadLycantropy: Boolean? = null,
    )
}

