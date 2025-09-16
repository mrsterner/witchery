package dev.sterner.witchery.handler.affliction

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.payload.RefreshDimensionsS2CPayload
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeInstance
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
        AfflictionPlayerAttachment.batchUpdate(player) {
            var result = setLevel(AfflictionTypes.LYCANTHROPY, level)

            if (level == 0) {
                result = result
                    .withAbilityIndex(-1)
                    .withWolfForm(false)
                    .withWolfManForm(false)
                    .withWolfPack(0)
            }

            result
        }

        if (level == 0) {
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
        val currentData = AfflictionPlayerAttachment.getData(player)
        val currentLevel = currentData.getLevel(AfflictionTypes.LYCANTHROPY)
        val nextLevel = currentLevel + 1

        if (nextLevel > 10) return

        if (nextLevel > 2 && !canLevelUp(player, currentData, nextLevel)) return


        setLevel(player, nextLevel)

        player.sendSystemMessage(Component.literal("Werewolf Level Up: $nextLevel"))
        updateModifiers(player, currentData.isWolfForm(), currentData.isWolfManForm())
    }


    private fun canPerformQuest(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = AfflictionPlayerAttachment.getData(player)

        if (data.getLevel(AfflictionTypes.LYCANTHROPY) != targetLevel) {
            return false
        }

        val requiredAdvancement = LEVEL_REQUIREMENTS[targetLevel]?.advancement ?: return false
        return TornPageItem.Companion.hasAdvancement(player, requiredAdvancement) //Nothing to do with the actual TornPage
    }

    //To go from Level 8 -> 9
    fun increaseKilledPiglin(player: ServerPlayer) {
        if (!canPerformQuest(player, 8)) {
            return
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player, sync = false) {
            incrementPigmenKilled()
        }

        checkAndLevelUp(player, newData)
    }

    //To go from Level 4 -> 5
    fun setHasKilledHuntsman(player: ServerPlayer){
        if (!canPerformQuest(player, 4)) {
            return
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player, sync = false) {
            withKilledHornedOne(true)
        }

        checkAndLevelUp(player, newData)
    }

    //To go from Level 3 -> 4
    fun increaseKilledWolf(player: ServerPlayer) {
        if (!canPerformQuest(player, 3)) {
            return
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player, sync = false) {
            incrementKilledWolves()
        }

        checkAndLevelUp(player, newData)
    }

    //To go from Level 2 -> 3
    fun increaseKilledSheep(player: ServerPlayer) {
        if (!canPerformQuest(player, 2)) {
            return
        }

        val newData = AfflictionPlayerAttachment.batchUpdate(player, sync = false) {
            incrementKilledSheep()
        }

        checkAndLevelUp(player, newData)
    }

    /**
     * Check if requirements are met and level up if so
     */
    private fun checkAndLevelUp(player: ServerPlayer, data: AfflictionPlayerAttachment.Data) {
        val currentLevel = data.getLevel(AfflictionTypes.LYCANTHROPY)
        val nextLevel = currentLevel + 1

        if (nextLevel <= 10 && canLevelUp(player, data, nextLevel)) {
            VampireLeveling.increaseVampireLevel(player)
        }
    }

    fun removeAllModifiers(player: Player) {
        player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.let {
            it.removeModifier(KNOCKBACK_BONUS)
            it.removeModifier(KNOCKBACK_BONUS_2)
        }

        player.attributes.getInstance(Attributes.MOVEMENT_SPEED)?.let {
            it.removeModifier(SPEED_BONUS)
            it.removeModifier(SPEED_BONUS_2)
        }

        player.attributes.getInstance(Attributes.ATTACK_DAMAGE)?.let {
            it.removeModifier(ATTACK_BONUS)
            it.removeModifier(ATTACK_BONUS_2)
        }

        player.attributes.getInstance(Attributes.STEP_HEIGHT)?.removeModifier(STEP_HEIGHT_BONUS)
        player.attributes.getInstance(Attributes.SAFE_FALL_DISTANCE)?.removeModifier(SAFE_HEIGHT)
        player.attributes.getInstance(Attributes.MAX_HEALTH)?.removeModifier(HEALTH_BONUS)
        player.attributes.getInstance(Attributes.ARMOR)?.removeModifier(RESIST_BONUS)
        player.attributes.getInstance(Attributes.ARMOR_TOUGHNESS)?.removeModifier(RESIST_TOUGH_BONUS)
    }

    fun updateModifiers(player: Player, wolf: Boolean, wolfMan: Boolean) {
        removeAllModifiers(player)

        if (wolf) {
            addModifierSafely(player.attributes.getInstance(Attributes.ATTACK_DAMAGE), ATTACK_BONUS)
            addModifierSafely(player.attributes.getInstance(Attributes.MOVEMENT_SPEED), SPEED_BONUS)
            addModifierSafely(player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK), KNOCKBACK_BONUS)
            addModifierSafely(player.attributes.getInstance(Attributes.STEP_HEIGHT), STEP_HEIGHT_BONUS)

        } else if (wolfMan) {
            addModifierSafely(player.attributes.getInstance(Attributes.ATTACK_DAMAGE), ATTACK_BONUS_2)
            addModifierSafely(player.attributes.getInstance(Attributes.MOVEMENT_SPEED), SPEED_BONUS_2)
            addModifierSafely(player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK), KNOCKBACK_BONUS_2)
            addModifierSafely(player.attributes.getInstance(Attributes.STEP_HEIGHT), STEP_HEIGHT_BONUS)
            addModifierSafely(player.attributes.getInstance(Attributes.SAFE_FALL_DISTANCE), SAFE_HEIGHT)
            addModifierSafely(player.attributes.getInstance(Attributes.MAX_HEALTH), HEALTH_BONUS)
            addModifierSafely(player.attributes.getInstance(Attributes.ARMOR), RESIST_BONUS)
            addModifierSafely(player.attributes.getInstance(Attributes.ARMOR_TOUGHNESS), RESIST_TOUGH_BONUS)
        }
    }

    private fun addModifierSafely(attributeInstance: AttributeInstance?, modifier: AttributeModifier) {
        attributeInstance?.let { attribute ->
            if (!attribute.hasModifier(modifier.id)) {
                attribute.addPermanentModifier(modifier)
            }
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

    private fun canLevelUp(player: ServerPlayer, data: AfflictionPlayerAttachment.Data, targetLevel: Int): Boolean {
        if (targetLevel == 1) {
            return true
        }
        if (targetLevel > 10) {
            return false
        }
        val requirement = LEVEL_REQUIREMENTS[targetLevel] ?: return false

        return ((requirement.threeGold?.let { data.hasGivenGold() == it } ?: true) &&
                (requirement.killedSheep?.let { data.getKilledSheep() >= it } ?: true) &&
                (requirement.killedWolves?.let { data.getKilledWolves() >= it } ?: true) &&
                (requirement.killHornedOne?.let { data.hasKilledHornedOne() == it } ?: true) &&
                (requirement.airSlayMonster?.let { data.getAirSlayMonster() == it } ?: true) &&
                (requirement.nightHowl?.let { data.getNightHowl() == it } ?: true) &&
                (requirement.wolfPack?.let { data.getWolfPack() == it } ?: true) &&
                (requirement.pigmenKilled?.let { data.getPigmenKilled() == it } ?: true) &&
                (requirement.spreadLycantropy?.let { data.hasSpreadLycanthropy() == it } ?: true)
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