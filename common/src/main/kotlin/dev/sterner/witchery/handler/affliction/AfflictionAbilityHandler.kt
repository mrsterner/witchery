package dev.sterner.witchery.handler.affliction

import dev.architectury.event.EventResult
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.client.screen.AbilitySelectionScreen
import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.ability.AbilityHandler
import dev.sterner.witchery.handler.ability.AbilityScrollHandler
import dev.sterner.witchery.payload.AfflictionAbilitySelectionC2SPayload
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object AfflictionAbilityHandler : AbilityHandler {

    override val abilityIndex: Int
        get() = AfflictionPlayerAttachment.getData(Minecraft.getInstance().player!!).getAbilityIndex()

    override fun getAbilities(player: Player): List<AfflictionAbility> {
        val data = AfflictionPlayerAttachment.getData(player)
        val selectedIds = data.getSelectedAbilities()

        if (selectedIds.isEmpty()) {
            val defaultAbilities = getAllAvailableAbilities(player).take(5)
            return defaultAbilities
        }

        val allAvailable = getAllAvailableAbilities(player)
        val result = selectedIds.mapNotNull { id ->
            allAvailable.find { it.id == id }
        }

        return result
    }

    fun getAllAvailableAbilities(player: Player): List<AfflictionAbility> {
        val data = AfflictionPlayerAttachment.getData(player)

        val vampLevel = data.getLevel(AfflictionTypes.VAMPIRISM)
        val wereLevel = data.getLevel(AfflictionTypes.LYCANTHROPY)

        val vampAbilities: List<VampireAbility> = VampireAbility.entries.filter { it.isAvailable(vampLevel) }
        val wereAbilities: List<WerewolfAbility> = WerewolfAbility.entries.filter { it.isAvailable(wereLevel) }

        return (vampAbilities + wereAbilities)
    }

    override fun setAbilityIndex(player: Player, index: Int) {
        val abilities = getAbilities(player)
        val clampedIndex = index.coerceIn(-1, abilities.size - 1)

        updateAbilityIndex(player, clampedIndex)
        if (player.level().isClientSide) {
            NetworkManager.sendToServer(AfflictionAbilitySelectionC2SPayload(clampedIndex))
        }
    }

    override fun updateAbilityIndex(player: Player, index: Int) {
        AfflictionPlayerAttachment.batchUpdate(player) {
            withAbilityIndex(index)
        }
    }

    fun updateSelectedAbilities(player: Player, abilities: List<String>) {

        AfflictionPlayerAttachment.batchUpdate(player) {
            withSelectedAbilities(abilities)
        }

        if (!player.level().isClientSide && player is ServerPlayer) {
            AfflictionPlayerAttachment.sync(player, AfflictionPlayerAttachment.getData(player))
        }
    }

    fun scroll(minecraft: Minecraft?, x: Double, y: Double): EventResult {
        val player = minecraft?.player ?: return EventResult.pass()

        val abilities = getAbilities(player)
        if (abilities.isEmpty()) return EventResult.pass()

        return AbilityScrollHandler().handleScroll(player, y, this)
    }

    fun getSelectedAbility(player: Player): AfflictionAbility? {
        val abilities = getAbilities(player)
        val index = AfflictionPlayerAttachment.getData(player).getAbilityIndex()
        return abilities.getOrNull(index)
    }

    fun useSelectedAbility(player: Player): Boolean {
        val ability = getSelectedAbility(player) ?: return false
        if (AbilityCooldownManager.isOnCooldown(player, ability)) return false

        val used = ability.use(player)

        return used
    }

    fun openSelectionScreen(player: Player) {
        if (player.level().isClientSide) {
            Minecraft.getInstance().setScreen(AbilitySelectionScreen(player))
        }
    }
}