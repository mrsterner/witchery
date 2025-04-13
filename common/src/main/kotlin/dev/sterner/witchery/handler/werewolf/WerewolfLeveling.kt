package dev.sterner.witchery.handler.werewolf

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.werewolf.WerewolfLevelRequirements.LEVEL_REQUIREMENTS
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

object WerewolfLeveling {

    private val KNOCKBACK_BONUS = AttributeModifier(Witchery.id("werewolf_knockback"), 0.5, AttributeModifier.Operation.ADD_VALUE)
    private val KNOCKBACK_BONUS_2 = AttributeModifier(Witchery.id("werewolf_knockback_2"), 0.6, AttributeModifier.Operation.ADD_VALUE)
    private val ATTACK_BONUS = AttributeModifier(Witchery.id("werewolf_damage"), 0.5, AttributeModifier.Operation.ADD_VALUE)
    private val ATTACK_BONUS_2 = AttributeModifier(Witchery.id("werewolf_damage_2"), 0.75, AttributeModifier.Operation.ADD_VALUE)
    private val SPEED_BONUS = AttributeModifier(Witchery.id("werewolf_speed"), 0.5, AttributeModifier.Operation.ADD_VALUE)
    private val SPEED_BONUS_2 = AttributeModifier(Witchery.id("werewolf_speed_2"), 0.75, AttributeModifier.Operation.ADD_VALUE)
    private val STEP_HEIGHT_BONUS = AttributeModifier(Witchery.id("werewolf_step"), 0.75, AttributeModifier.Operation.ADD_VALUE)
    private val JUMP_HEIGHT_BONUS = AttributeModifier(Witchery.id("werewolf_jump"), 1.1, AttributeModifier.Operation.ADD_VALUE)
    private val HEALTH_BONUS = AttributeModifier(Witchery.id("werewolf_health"), 10.0, AttributeModifier.Operation.ADD_VALUE)

    private val RESIST_BONUS = AttributeModifier(Witchery.id("werewolf_resist"), 5.0, AttributeModifier.Operation.ADD_VALUE)
    private val RESIST_TOUGH_BONUS = AttributeModifier(Witchery.id("werewolf_resist_tough"), 5.0, AttributeModifier.Operation.ADD_VALUE)

    /**
     * Will level upp a vampire-player if they for fills the requirements to do so.
     */
    @JvmStatic
    fun increaseWerewolfLevel(player: ServerPlayer) {
        val data = WerewolfPlayerAttachment.getData(player)
        val nextLevel = data.werewolfLevel + 1

        if (WerewolfLevelRequirements.canLevelUp(player, nextLevel)) {
            WerewolfPlayerAttachment.setData(player, data.copy(werewolfLevel = nextLevel))
            player.sendSystemMessage(Component.literal("Werewolf Level Up: $nextLevel"))
            updateModifiers(player, data.isWolfFormActive, data.isWolfManFormActive)
        }
    }


    fun canPerformQuest(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = WerewolfPlayerAttachment.getData(player)

        if (data.werewolfLevel != targetLevel) {
            return false
        }

        val requiredAdvancement = LEVEL_REQUIREMENTS[targetLevel]?.advancement ?: return false
        return TornPageItem.hasAdvancement(player, requiredAdvancement) //Nothing to do with the actual TornPage
    }

    //To go from Level 8 -> 9
    fun increaseKilledPiglin(player: ServerPlayer) {
        if(!canPerformQuest(player, 8)) {
            return
        }

        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(pigmenKilled = data.pigmenKilled + 1))

        increaseWerewolfLevel(player)
    }

    //To go from Level 3 -> 4
    fun increaseKilledWolf(player: ServerPlayer) {
        if(!canPerformQuest(player, 3)) {
            return
        }

        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(killedWolves = data.killedWolves + 1))

        increaseWerewolfLevel(player)
    }

    //To go from Level 2 -> 3
    fun increaseKilledSheep(player: ServerPlayer) {
        if(!canPerformQuest(player, 2)) {
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
        player.attributes.getInstance(Attributes.JUMP_STRENGTH)?.removeModifier(JUMP_HEIGHT_BONUS)

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
            player.attributes.getInstance(Attributes.JUMP_STRENGTH)?.addPermanentModifier(JUMP_HEIGHT_BONUS)
            player.attributes.getInstance(Attributes.MAX_HEALTH)?.addPermanentModifier(HEALTH_BONUS)

            player.attributes.getInstance(Attributes.ARMOR)?.addPermanentModifier(RESIST_BONUS)
            player.attributes.getInstance(Attributes.ARMOR_TOUGHNESS)?.addPermanentModifier(RESIST_TOUGH_BONUS)
        }
    }

}