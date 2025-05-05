package dev.sterner.witchery.handler.werewolf

import dev.architectury.event.EventResult
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.handler.ability.AbilityHandler
import dev.sterner.witchery.handler.ability.AbilityScrollHandler
import dev.sterner.witchery.handler.ability.WerewolfAbility
import dev.sterner.witchery.payload.WerewolfAbilitySelectionC2SPayload
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

object WerewolfAbilityHandler : AbilityHandler<WerewolfAbility> {
    override val abilityIndex: Int
        get() = WerewolfPlayerAttachment.getData(Minecraft.getInstance().player!!).abilityIndex

    override fun getLevel(player: Player): Int =
        WerewolfPlayerAttachment.getData(player).getWerewolfLevel()

    override fun getAbilities(player: Player): List<WerewolfAbility> =
        WerewolfAbility.entries.filter { it.isAvailable(player) }

    override fun setAbilityIndex(player: Player, index: Int) {
        updateAbilityIndex(player, index)
        if (player.level().isClientSide) {
            NetworkManager.sendToServer(WerewolfAbilitySelectionC2SPayload(index))
        }
    }

    override fun updateAbilityIndex(player: Player, index: Int) {
        val data = WerewolfPlayerAttachment.getData(player)
        WerewolfPlayerAttachment.setData(player, data.copy(abilityIndex = index))
    }

    fun scroll(minecraft: Minecraft?, x: Double, y: Double): EventResult {
        val player = minecraft?.player ?: return EventResult.pass()

        // Get player's current abilities
        val abilities = getAbilities(player)
        if (abilities.isEmpty()) return EventResult.pass()

        // Handle scrolling with modified logic
        return AbilityScrollHandler().handleScroll(player, y, this)
    }

}