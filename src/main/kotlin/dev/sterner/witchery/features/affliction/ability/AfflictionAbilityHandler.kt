package dev.sterner.witchery.features.affliction.ability

import dev.sterner.witchery.client.screen.AbilitySelectionScreen
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.lich.LichdomAbility
import dev.sterner.witchery.features.affliction.vampire.VampireAbility
import dev.sterner.witchery.features.affliction.werewolf.WerewolfAbility
import dev.sterner.witchery.features.death.DeathAbilityHandler
import dev.sterner.witchery.features.death.DeathTransformationHelper
import dev.sterner.witchery.network.AfflictionAbilitySelectionC2SPayload
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object AfflictionAbilityHandler : AbilityHandler {

    override val abilityIndex: Int
        get() = AfflictionPlayerAttachment.getData(Minecraft.getInstance().player!!).getAbilityIndex()

    override fun getAbilities(player: Player): List<AfflictionAbility> {
        if (DeathTransformationHelper.isDeath(player)) {
            return DeathAbilityHandler.getAbilities(player)
        }

        val data = AfflictionPlayerAttachment.getData(player)
        val selectedIds = data.getSelectedAbilities()

        val allAvailable = getAllAvailableAbilities(player)
        return selectedIds.mapNotNull { id ->
            allAvailable.find { it.id == id }
        }
    }

    fun getAllAvailableAbilities(player: Player): List<AfflictionAbility> {
        if (DeathTransformationHelper.isDeath(player)) {
            return DeathAbilityHandler.getAbilities(player)
        }

        val data = AfflictionPlayerAttachment.getData(player)

        val vampLevel = data.getLevel(AfflictionTypes.VAMPIRISM)
        val wereLevel = data.getLevel(AfflictionTypes.LYCANTHROPY)
        val lichLevel = data.getLevel(AfflictionTypes.LICHDOM)

        val vampAbilities: List<VampireAbility> = VampireAbility.entries.filter { it.isAvailable(player, vampLevel) }
        val wereAbilities: List<WerewolfAbility> = WerewolfAbility.entries.filter { ability ->
            ability.isAvailable(player, wereLevel) &&
                    when (ability) {
                        WerewolfAbility.WOLF_FORM,
                        WerewolfAbility.WEREWOLF_FORM -> WerewolfAbility.Companion.hasMoonCharm(player)

                        else -> true
                    }
        }
        val lichAbilities: List<LichdomAbility> = LichdomAbility.entries.filter { it.isAvailable(player, lichLevel) }

        return (vampAbilities + wereAbilities + lichAbilities)
    }

    override fun setAbilityIndex(player: Player, index: Int) {
        if (DeathTransformationHelper.isDeath(player)) {
            DeathAbilityHandler.setAbilityIndex(player, index)
            return
        }

        updateAbilityIndex(player, index)
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(AfflictionAbilitySelectionC2SPayload(index))
        }
    }

    override fun updateAbilityIndex(player: Player, index: Int) {
        if (DeathTransformationHelper.isDeath(player)) {
            DeathAbilityHandler.updateAbilityIndex(player, index)
            return
        }

        AfflictionPlayerAttachment.smartUpdate(player) {
            withAbilityIndex(index)
        }
    }

    fun addAbilityOnLevelUp(player: Player, newLevel: Int, affliction: AfflictionTypes, force: Boolean = false) {
        val currentSelectedIds = AfflictionPlayerAttachment.getData(player).getSelectedAbilities().toMutableList()

        val newAbilities = when (affliction) {
            AfflictionTypes.VAMPIRISM -> {
                VampireAbility.entries.filter {
                    (it.requiredLevel == newLevel && it.affliction == affliction) || force
                }
            }

            AfflictionTypes.LYCANTHROPY -> {
                WerewolfAbility.entries.filter {
                    (it.requiredLevel == newLevel && it.affliction == affliction) || force
                }
            }

            AfflictionTypes.LICHDOM -> {
                LichdomAbility.entries.filter {
                    (it.requiredLevel == newLevel && it.affliction == affliction) || force
                }
            }
        }

        newAbilities.forEach { newAbility ->
            if (!currentSelectedIds.contains(newAbility.id)) {
                if (currentSelectedIds.size < 5) {
                    currentSelectedIds.add(newAbility.id)
                }
            }
        }

        if (currentSelectedIds.isNotEmpty()) {
            updateSelectedAbilities(player, currentSelectedIds)
        }
    }

    fun updateSelectedAbilities(player: Player, abilities: List<String>) {
        AfflictionPlayerAttachment.smartUpdate(player) {
            withSelectedAbilities(abilities).withAbilityIndex(-1)
        }
    }

    fun scroll(minecraft: Minecraft?, scrollDeltaX: Double, scrollDeltaY: Double): Boolean {
        val player = minecraft?.player ?: return false

        if (DeathTransformationHelper.isDeath(player)) {
            return DeathAbilityHandler.scroll(minecraft, scrollDeltaX, scrollDeltaY)
        }

        val abilities = getAbilities(player)
        if (abilities.isEmpty()) return false

        return AbilityScrollHandler().handleScroll(player, scrollDeltaY, this)
    }

    fun getSelectedAbility(player: Player): AfflictionAbility? {
        if (DeathTransformationHelper.isDeath(player)) {
            return DeathAbilityHandler.getSelectedAbility(player)
        }

        val abilities = getAbilities(player)
        val index = AfflictionPlayerAttachment.getData(player).getAbilityIndex()
        return abilities.getOrNull(index)
    }

    fun useSelectedAbility(player: Player): Boolean {
        if (DeathTransformationHelper.isDeath(player)) {
            return DeathAbilityHandler.useSelectedAbility(player)
        }

        val ability = getSelectedAbility(player) ?: return false
        if (AbilityCooldownManager.isOnCooldown(player, ability)) return false

        return ability.use(player)
    }

    fun openSelectionScreen(player: Player) {
        if (player.level().isClientSide) {
            Minecraft.getInstance().setScreen(AbilitySelectionScreen(player))
        }
    }
}