package dev.sterner.witchery.handler.vampire

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.TornPageItem
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.setData
import dev.sterner.witchery.handler.vampire.VampireLevelRequirements.LEVEL_REQUIREMENTS
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos

object VampireLeveling {

    private val KNOCKBACK_BONUS = AttributeModifier(Witchery.id("vampire_knockback"), 0.5, AttributeModifier.Operation.ADD_VALUE)

    @JvmStatic
    fun increaseVampireLevel(player: ServerPlayer) {
        val data = getData(player)
        val nextLevel = data.vampireLevel + 1

        if (VampireLevelRequirements.canLevelUp(player, nextLevel)) {
            setData(player, data.copy(vampireLevel = nextLevel))
            setMaxBlood(player, nextLevel)
            player.sendSystemMessage(Component.literal("Vampire Level Up: $nextLevel"))
            updateModifiers(player, nextLevel)
        }
    }

    fun setMaxBlood(player: Player, level: Int) {
        val maxBlood = when (level) {
            1 -> 900
            2 -> 1200
            3 -> 1500
            4 -> 1500
            5 -> 1800
            6 -> 2100
            7 -> 2400
            8 -> 2700
            9 -> 3000
            10 -> 3600
            else -> 0
        }
        val bloodData = BloodPoolLivingEntityAttachment.getData(player)
        BloodPoolLivingEntityAttachment.setData(player, bloodData.copy(maxBlood = maxBlood))
    }

    fun updateModifiers(player: Player, level: Int) {
        player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)?.removeModifier(KNOCKBACK_BONUS)
        if (level >= 3) {
            player.attributes.getInstance(Attributes.ATTACK_KNOCKBACK)
                ?.addPermanentModifier(KNOCKBACK_BONUS)
        }
    }

    fun canPerformQuest(player: ServerPlayer, targetLevel: Int): Boolean {
        val data = getData(player)

        if (data.vampireLevel != targetLevel) {
            return false
        }

        val requiredAdvancement = LEVEL_REQUIREMENTS[targetLevel]?.advancement ?: return false
        return TornPageItem.hasAdvancement(player, requiredAdvancement)
    }

    //To go from Level 1 -> 2 is handled in BloodPoolLivingEntityAttachment

    //To go from Level 2 -> 3
    @JvmStatic
    fun increaseVillagersHalfBlood(player: ServerPlayer, villager: Villager) {
        if(!canPerformQuest(player, 2)) {
            return
        }

        val data = getData(player)
        if (!data.villagersHalfBlood.contains(villager.uuid)) {
            val updatedList = data.villagersHalfBlood.toMutableList().apply { add(villager.uuid) }
            setData(player, data.copy(villagersHalfBlood = updatedList))
            increaseVampireLevel(player)
        }
    }

    @JvmStatic
    fun removeVillagerHalfBlood(player: Player, villager: Villager) {
        val data = getData(player)
        if (data.villagersHalfBlood.contains(villager.uuid)) {
            val updatedList = data.villagersHalfBlood.toMutableList().apply { remove(villager.uuid) }
            setData(player, data.copy(villagersHalfBlood = updatedList))
        }
    }

    //To go from Level 3 -> 4
    @JvmStatic
    fun increaseNightTicker(player: ServerPlayer) {
        if(!canPerformQuest(player, 3)) {
            return
        }

        val data = getData(player)
        setData(player, data.copy(nightTicker = data.nightTicker + 1), false)

        increaseVampireLevel(player)
    }

    @JvmStatic
    fun resetNightCounter(player: Player) {
        val data = getData(player)
        setData(player, data.copy(nightTicker = 0))
    }

    //To go from Level 4 -> 5
    @JvmStatic
    fun increaseUsedSunGrenades(player: ServerPlayer) {
        if(!canPerformQuest(player, 4)) {
            return
        }

        val data = getData(player)
        setData(player, data.copy(usedSunGrenades = data.usedSunGrenades + 1))

        increaseVampireLevel(player)
    }

    //To go from Level 5 -> 6
    @JvmStatic
    fun increaseKilledBlazes(player: ServerPlayer) {
        if(!canPerformQuest(player, 5)) {
            return
        }

        val data = getData(player)
        setData(player, data.copy(killedBlazes = data.killedBlazes + 1))

        increaseVampireLevel(player)
    }

    //To go from Level 6 -> 7 is handles in LilithEntity
    @JvmStatic
    fun givePoppy(player: ServerPlayer) {
        if(!canPerformQuest(player, 6)) {
            return
        }

        increaseVampireLevel(player)
    }

    //To go from Level 7 -> 8
    @JvmStatic
    fun addVillage(player: ServerPlayer, pos: ChunkPos) {
        if(!canPerformQuest(player, 7)) {
            return
        }

        val data = getData(player)
        val longPos = ChunkPos.asLong(pos.x, pos.z)
        if (!data.visitedVillages.contains(longPos)) {
            val updatedList = data.visitedVillages.toMutableList().apply { add(longPos) }
            setData(player, data.copy(visitedVillages = updatedList))
            increaseVampireLevel(player)
        }
    }

    @JvmStatic
    fun resetVillages(player: Player) {
        val data = getData(player)
        setData(player, data.copy(visitedVillages = mutableListOf()))
    }

    //To go from Level 8 -> 9
    @JvmStatic
    fun increaseTrappedVillagers(player: ServerPlayer) {
        if(!canPerformQuest(player, 8)) {
            return
        }

        val data = getData(player)
        setData(player, data.copy(trappedVillagers = data.trappedVillagers + 1))
        increaseVampireLevel(player)
    }
}