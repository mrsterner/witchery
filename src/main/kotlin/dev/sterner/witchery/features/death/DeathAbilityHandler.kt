package dev.sterner.witchery.features.death

import dev.sterner.witchery.features.affliction.ability.AbilityHandler
import dev.sterner.witchery.features.affliction.ability.AbilityCooldownManager
import dev.sterner.witchery.features.affliction.ability.AbilityScrollHandler
import dev.sterner.witchery.features.affliction.ability.AfflictionAbility
import dev.sterner.witchery.features.misc.MiscPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

object DeathAbilityHandler : AbilityHandler {

    override val abilityIndex: Int
        get() = DeathPlayerAttachment.getData(Minecraft.getInstance().player!!).deathAbilityIndex

    override fun getAbilities(player: Player): List<AfflictionAbility> {
        if (!DeathTransformationHelper.isDeath(player)) return emptyList()
        return DeathAbility.entries
    }

    override fun setAbilityIndex(player: Player, index: Int) {
        updateAbilityIndex(player, index)
    }

    override fun updateAbilityIndex(player: Player, index: Int) {
        val data = DeathPlayerAttachment.getData(player)
        DeathPlayerAttachment.setData(player, data.copy(deathAbilityIndex = index))
    }

    fun scroll(minecraft: Minecraft?, scrollDeltaX: Double, scrollDeltaY: Double): Boolean {
        val player = minecraft?.player ?: return false
        if (!DeathTransformationHelper.isDeath(player)) return false

        val abilities = getAbilities(player)
        if (abilities.isEmpty()) return false

        return AbilityScrollHandler().handleScroll(player, scrollDeltaY, this)
    }

    fun getSelectedAbility(player: Player): AfflictionAbility? {
        if (!DeathTransformationHelper.isDeath(player)) return null
        val abilities = getAbilities(player)
        val index = DeathPlayerAttachment.getData(player).deathAbilityIndex
        return abilities.getOrNull(index)
    }

    fun useSelectedAbility(player: Player): Boolean {
        if (!DeathTransformationHelper.isDeath(player)) return false
        val ability = getSelectedAbility(player) ?: return false
        if (AbilityCooldownManager.isOnCooldown(player, ability)) return false

        return ability.use(player)
    }
}