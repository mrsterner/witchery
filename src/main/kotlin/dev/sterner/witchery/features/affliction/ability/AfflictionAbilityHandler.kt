package dev.sterner.witchery.features.affliction.ability

import dev.sterner.witchery.client.screen.AbilitySelectionScreen
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.lich.LichdomAbility
import dev.sterner.witchery.features.affliction.vampire.VampireAbility
import dev.sterner.witchery.features.affliction.werewolf.WerewolfAbility
import dev.sterner.witchery.features.death.DeathAbility
import dev.sterner.witchery.features.death.DeathTransformationHelper
import dev.sterner.witchery.features.death.DeathTransformationHelper.hasDeathBoots
import dev.sterner.witchery.network.AfflictionAbilitySelectionC2SPayload
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object AfflictionAbilityHandler : AbilityHandler {

    override val abilityIndex: Int?
        get(){
            val player = Minecraft.getInstance().player ?: return null
            return AfflictionPlayerAttachment.getData(player).getAbilityIndex()
        }

    override fun getAbilities(player: Player): List<AfflictionAbility> {
        val data = AfflictionPlayerAttachment.getData(player)
        val selectedIds = data.getSelectedAbilities()

        val allAvailable = getAllAvailableAbilities(player)
        val noDeath: List<AfflictionAbility> = selectedIds.mapNotNull { id ->
            allAvailable.find { it.id == id }
        }
        if (DeathTransformationHelper.isDeath(player)) {
            noDeath.plus(DeathAbility.entries)
        } else if (hasDeathBoots(player)) {
            noDeath.plus(DeathAbility.FLUID_WALKING)
        }

        return noDeath
    }

    fun getAllAvailableAbilities(player: Player): List<AfflictionAbility> {
        var deathAbilities = listOf<DeathAbility>()
        if (DeathTransformationHelper.isDeath(player)) {
            deathAbilities = DeathAbility.entries
        } else if (hasDeathBoots(player)) {
            deathAbilities = listOf(DeathAbility.FLUID_WALKING)
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

        return (vampAbilities + wereAbilities + lichAbilities + deathAbilities)
    }

    override fun setAbilityIndex(player: Player, index: Int) {
        updateAbilityIndex(player, index)
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(AfflictionAbilitySelectionC2SPayload(index))
        }
    }

    override fun updateAbilityIndex(player: Player, index: Int) {
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

            AfflictionTypes.DEATH -> {
                DeathAbility.entries.filter {
                    false
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
        val abilities = getAbilities(player)
        if (abilities.isEmpty()) return false

        return AbilityScrollHandler().handleScroll(player, scrollDeltaY, this)
    }

    fun getSelectedAbility(player: Player): AfflictionAbility? {
        val abilities = getAbilities(player)
        val index = AfflictionPlayerAttachment.getData(player).getAbilityIndex()
        return abilities.getOrNull(index)
    }

    fun useSelectedAbility(player: Player): Boolean {
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