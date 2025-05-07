package dev.sterner.witchery.handler.werewolf

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.payload.RefreshDimensionsS2CPayload
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

object WerewolfLeveling {

    private val KNOCKBACK_BONUS =
        AttributeModifier(Witchery.id("werewolf_knockback"), 0.5, AttributeModifier.Operation.ADD_VALUE)
    private val KNOCKBACK_BONUS_2 =
        AttributeModifier(Witchery.id("werewolf_knockback_2"), 0.6, AttributeModifier.Operation.ADD_VALUE)
    private val ATTACK_BONUS =
        AttributeModifier(Witchery.id("werewolf_damage"), 0.5, AttributeModifier.Operation.ADD_VALUE)
    private val ATTACK_BONUS_2 =
        AttributeModifier(Witchery.id("werewolf_damage_2"), 0.75, AttributeModifier.Operation.ADD_VALUE)
    private val SPEED_BONUS =
        AttributeModifier(Witchery.id("werewolf_speed"), 0.1, AttributeModifier.Operation.ADD_VALUE)
    private val SPEED_BONUS_2 =
        AttributeModifier(Witchery.id("werewolf_speed_2"), 0.05, AttributeModifier.Operation.ADD_VALUE)
    private val STEP_HEIGHT_BONUS =
        AttributeModifier(Witchery.id("werewolf_step"), 0.75, AttributeModifier.Operation.ADD_VALUE)
    private val SAFE_HEIGHT =
        AttributeModifier(Witchery.id("werewolf_land"), 2.0, AttributeModifier.Operation.ADD_VALUE)
    private val HEALTH_BONUS =
        AttributeModifier(Witchery.id("werewolf_health"), 10.0, AttributeModifier.Operation.ADD_VALUE)
    private val RESIST_BONUS =
        AttributeModifier(Witchery.id("werewolf_resist"), 5.0, AttributeModifier.Operation.ADD_VALUE)
    private val RESIST_TOUGH_BONUS =
        AttributeModifier(Witchery.id("werewolf_resist_tough"), 5.0, AttributeModifier.Operation.ADD_VALUE)

    @JvmStatic
    fun setLevel(player: ServerPlayer, level: Int) {
        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(werewolfLevel = level))
        if (level == 0) {
            WerewolfAbilityHandler.setAbilityIndex(player, -1)
            TransformationHandler.removeForm(player)
        }
        val wolf = TransformationHandler.isWolf(player)
        val were = TransformationHandler.isWerewolf(player)
        updateModifiers(player, wolf = wolf, wolfMan = were)
        player.refreshDimensions()
        WitcheryPayloads.sendToPlayers(
            player.level(),
            player.blockPosition(),
            RefreshDimensionsS2CPayload()
        )
    }

    /**
     * Will level up a werewolf player if they fulfill the requirements.
     */
    @JvmStatic
    fun increaseWerewolfLevel(player: ServerPlayer) {
        val data = WerewolfPlayerAttachment.getData(player)
        val nextLevel = data.getWerewolfLevel() + 1

        if (!canLevelUp(player, nextLevel)) return

        val updatedData = data.copy(werewolfLevel = nextLevel)
        WerewolfPlayerAttachment.setData(player, updatedData)
        player.sendSystemMessage(Component.literal("Werewolf Level Up: $nextLevel"))
        updateModifiers(player, data.isWolfFormActive, data.isWolfManFormActive)
    }

    private fun canPerformQuest(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = WerewolfPlayerAttachment.getData(player)

        if (data.getWerewolfLevel() != targetLevel) {
            return false
        }

        val requiredAdvancement = LEVEL_REQUIREMENTS[targetLevel]?.advancement ?: return false
        return TornPageItem.hasAdvancement(player, requiredAdvancement) //Nothing to do with the actual TornPage
    }

    //To go from Level 8 -> 9
    fun increaseKilledPiglin(player: ServerPlayer) {
        if (!canPerformQuest(player, 8)) {
            return
        }

        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(pigmenKilled = data.pigmenKilled + 1))

        increaseWerewolfLevel(player)
    }

    //To go from Level 3 -> 4
    fun increaseKilledWolf(player: ServerPlayer) {
        if (!canPerformQuest(player, 3)) {
            return
        }

        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(killedWolves = data.killedWolves + 1))

        increaseWerewolfLevel(player)
    }

    //To go from Level 2 -> 3
    fun increaseKilledSheep(player: ServerPlayer) {
        if (!canPerformQuest(player, 2)) {
            return
        }

        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(killedSheep = data.killedSheep + 1))

        increaseWerewolfLevel(player)
    }

    /**
     * When the player werewolf-level changes this will reset and add potential attributes
     */
    fun updateModifiers(player: Player, wolf: Boolean, wolfMan: Boolean) {
        player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.removeModifier(KNOCKBACK_BONUS)
        player.attributes.getInstance(Attributes.MOVEMENT_SPEED)?.removeModifier(SPEED_BONUS)
        player.attributes.getInstance(Attributes.ATTACK_DAMAGE)?.removeModifier(ATTACK_BONUS)
        player.attributes.getInstance(Attributes.STEP_HEIGHT)?.removeModifier(STEP_HEIGHT_BONUS)
        player.attributes.getInstance(Attributes.SAFE_FALL_DISTANCE)?.removeModifier(SAFE_HEIGHT)

        player.attributes.getInstance(Attributes.ATTACK_DAMAGE)?.removeModifier(ATTACK_BONUS_2)
        player.attributes.getInstance(Attributes.MOVEMENT_SPEED)?.removeModifier(SPEED_BONUS_2)
        player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.removeModifier(KNOCKBACK_BONUS_2)
        player.attributes.getInstance(Attributes.MAX_HEALTH)?.removeModifier(HEALTH_BONUS)
        player.attributes.getInstance(Attributes.ARMOR)?.removeModifier(RESIST_BONUS)
        player.attributes.getInstance(Attributes.ARMOR_TOUGHNESS)?.removeModifier(RESIST_TOUGH_BONUS)

        if (wolf) {
            player.attributes.getInstance(Attributes.ATTACK_DAMAGE)?.addPermanentModifier(ATTACK_BONUS)
            player.attributes.getInstance(Attributes.MOVEMENT_SPEED)?.addPermanentModifier(SPEED_BONUS)
            player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.addPermanentModifier(KNOCKBACK_BONUS)

            player.attributes.getInstance(Attributes.STEP_HEIGHT)?.addPermanentModifier(STEP_HEIGHT_BONUS)
        } else if (wolfMan) {
            player.attributes.getInstance(Attributes.ATTACK_DAMAGE)?.addPermanentModifier(ATTACK_BONUS_2)
            player.attributes.getInstance(Attributes.MOVEMENT_SPEED)?.addPermanentModifier(SPEED_BONUS_2)
            player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.addPermanentModifier(KNOCKBACK_BONUS_2)

            player.attributes.getInstance(Attributes.STEP_HEIGHT)?.addPermanentModifier(STEP_HEIGHT_BONUS)
            player.attributes.getInstance(Attributes.SAFE_FALL_DISTANCE)?.addPermanentModifier(SAFE_HEIGHT)
            player.attributes.getInstance(Attributes.MAX_HEALTH)?.addPermanentModifier(HEALTH_BONUS)

            player.attributes.getInstance(Attributes.ARMOR)?.addPermanentModifier(RESIST_BONUS)
            player.attributes.getInstance(Attributes.ARMOR_TOUGHNESS)?.addPermanentModifier(RESIST_TOUGH_BONUS)
        }
    }

    val LEVEL_REQUIREMENTS: Map<Int, Requirement> = mapOf(
        2 to Requirement(Witchery.id("werewolf/1"), threeGold = true),
        3 to Requirement(Witchery.id("werewolf/2"), killedSheep = 30),
        4 to Requirement(Witchery.id("werewolf/3"), killedWolves = 10),
        5 to Requirement(Witchery.id("werewolf/4"), killHornedOne = true),
        6 to Requirement(Witchery.id("werewolf/5"), airSlayMonster = 10),
        7 to Requirement(Witchery.id("werewolf/6"), nightHowl = 10),
        8 to Requirement(Witchery.id("werewolf/7"), wolfPack = 6),
        9 to Requirement(Witchery.id("werewolf/8"), pigmenKilled = 30),
        10 to Requirement(Witchery.id("werewolf/9"), spreadLycantropy = true)
    )

    private fun canLevelUp(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = WerewolfPlayerAttachment.getData(player)
        if (targetLevel == 1) {
            return true
        }
        if (targetLevel > 10) {
            return false
        }
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

    data class Requirement(
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