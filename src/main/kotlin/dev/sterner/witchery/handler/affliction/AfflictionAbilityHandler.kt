package dev.sterner.witchery.handler.affliction

import dev.sterner.witchery.client.screen.AbilitySelectionScreen
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.ability.AbilityHandler
import dev.sterner.witchery.handler.ability.AbilityScrollHandler
import dev.sterner.witchery.handler.affliction.lich.LichdomAbility
import dev.sterner.witchery.handler.affliction.vampire.VampireAbility
import dev.sterner.witchery.handler.affliction.werewolf.WerewolfAbility
import dev.sterner.witchery.payload.AfflictionAbilitySelectionC2SPayload
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object AfflictionAbilityHandler : AbilityHandler {

    override val abilityIndex: Int
        get() = AfflictionPlayerAttachment.getData(Minecraft.getInstance().player!!).getAbilityIndex()

    override fun getAbilities(player: Player): List<AfflictionAbility> {
        val data = AfflictionPlayerAttachment.getData(player)
        val selectedIds = data.getSelectedAbilities()

        val allAvailable = getAllAvailableAbilities(player)
        return selectedIds.mapNotNull { id ->
            allAvailable.find { it.id == id }
        }
    }

    fun getAllAvailableAbilities(player: Player): List<AfflictionAbility> {
        val data = AfflictionPlayerAttachment.getData(player)

        val vampLevel = data.getLevel(AfflictionTypes.VAMPIRISM)
        val wereLevel = data.getLevel(AfflictionTypes.LYCANTHROPY)
        val lichLevel = data.getLevel(AfflictionTypes.LICHDOM)

        val vampAbilities: List<VampireAbility> = VampireAbility.entries.filter { it.isAvailable(vampLevel) }
        val wereAbilities: List<WerewolfAbility> = WerewolfAbility.entries.filter { ability ->
            ability.isAvailable(wereLevel) &&
                    when (ability) {
                        WerewolfAbility.WOLF_FORM,
                        WerewolfAbility.WEREWOLF_FORM -> WerewolfAbility.hasMoonCharm(player)

                        else -> true
                    }
        }
        val lichAbilities: List<LichdomAbility> = LichdomAbility.entries.filter { it.isAvailable(lichLevel) }

        return (vampAbilities + wereAbilities + lichAbilities)
    }

    override fun setAbilityIndex(player: Player, index: Int) {
        updateAbilityIndex(player, index)
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(AfflictionAbilitySelectionC2SPayload(index))
        }
    }

    override fun updateAbilityIndex(player: Player, index: Int) {
        AfflictionPlayerAttachment.batchUpdate(player) {
            withAbilityIndex(index)
        }
    }

    fun addAbilityOnLevelUp(player: Player, newLevel: Int, affliction: AfflictionTypes) {
        val currentSelectedIds = AfflictionPlayerAttachment.getData(player).getSelectedAbilities().toMutableList()

        val newAbilities = when (affliction) {
            AfflictionTypes.VAMPIRISM -> {
                VampireAbility.entries.filter {
                    it.requiredLevel == newLevel && it.affliction == affliction
                }
            }

            AfflictionTypes.LYCANTHROPY -> {
                WerewolfAbility.entries.filter {
                    it.requiredLevel == newLevel && it.affliction == affliction
                }
            }

            AfflictionTypes.LICHDOM -> {
                LichdomAbility.entries.filter {
                    it.requiredLevel == newLevel && it.affliction == affliction
                }
            }


            else -> emptyList()
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

        AfflictionPlayerAttachment.batchUpdate(player) {
            withSelectedAbilities(abilities).withAbilityIndex(-1)
        }

        if (!player.level().isClientSide && player is ServerPlayer) {
            AfflictionPlayerAttachment.sync(player, AfflictionPlayerAttachment.getData(player))
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

        val used = ability.use(player)

        return used
    }

    fun openSelectionScreen(player: Player) {
        if (player.level().isClientSide) {
            Minecraft.getInstance().setScreen(AbilitySelectionScreen(player))
        }
    }
}

