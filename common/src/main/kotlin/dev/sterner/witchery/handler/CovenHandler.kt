package dev.sterner.witchery.handler

import dev.sterner.witchery.entity.CovenWitchEntity
import dev.sterner.witchery.platform.CovenPlayerAttachment
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

object CovenHandler {

    /**
     * Adds a Coven-Witch to the player's coven. Discards the Witch
     */
    fun addWitchToCoven(player: ServerPlayer, witch: CovenWitchEntity) {
        val data = CovenPlayerAttachment.getData(player)
        val newList = data.covenWitchList.toMutableList()

        val tag = CompoundTag()
        if (witch.saveAsPassenger(tag)) {
            newList.add(tag)
            CovenPlayerAttachment.setData(player, data.copy(covenWitchList = newList))
            witch.discard()
        }
    }

    /**
     * Get all the players Coven-Witches
     */
    fun getWitchesFromCoven(player: Player): List<CovenWitchEntity> {
        val data = CovenPlayerAttachment.getData(player)
        val level = player.level()

        return data.covenWitchList.mapNotNull { tag ->
            EntityType.loadEntityRecursive(tag, level) { it } as? CovenWitchEntity
        }
    }

    /**
     * Takes a Coven-Witch from a specific index in the players list of coven witches
     */
    fun summonWitchFromCoven(player: Player, index: Int, summonTo: Vec3): CovenWitchEntity? {
        val data = CovenPlayerAttachment.getData(player)
        val tag = data.covenWitchList.getOrNull(index) ?: return null
        val level = player.level()

        val mob = EntityType.loadEntityRecursive(tag.copy(), level) { it } as? CovenWitchEntity ?: return null
        mob.moveTo(summonTo.x, summonTo.y, summonTo.z)
        level.addFreshEntity(mob)

        return mob
    }

    /**
     * Removes a coven-witch from its index
     */
    fun removeWitchFromCoven(player: ServerPlayer, index: Int): Boolean {
        val data = CovenPlayerAttachment.getData(player)
        val list = data.covenWitchList.toMutableList()

        if (index in list.indices) {
            list.removeAt(index)
            CovenPlayerAttachment.setData(player, data.copy(covenWitchList = list))
            return true
        }
        return false
    }

    fun getSummonableWitchCount(player: Player): Int {
        return CovenPlayerAttachment.getData(player).covenWitchList.size
    }
}
