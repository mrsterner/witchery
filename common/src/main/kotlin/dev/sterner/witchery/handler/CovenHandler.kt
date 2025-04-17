package dev.sterner.witchery.handler

import dev.sterner.witchery.entity.CovenWitchEntity
import dev.sterner.witchery.platform.CovenPlayerAttachment
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.Vec3

object CovenHandler {

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

    fun getWitchesFromCoven(player: ServerPlayer): List<CovenWitchEntity> {
        val data = CovenPlayerAttachment.getData(player)
        val level = player.level()

        return data.covenWitchList.mapNotNull { tag ->
            EntityType.loadEntityRecursive(tag, level) { it } as? CovenWitchEntity
        }
    }

    fun summonWitchFromCoven(player: ServerPlayer, index: Int, summonTo: Vec3): CovenWitchEntity? {
        val data = CovenPlayerAttachment.getData(player)
        val tag = data.covenWitchList.getOrNull(index) ?: return null
        val level = player.level()

        val mob = EntityType.loadEntityRecursive(tag.copy(), level) { it } as? CovenWitchEntity ?: return null
        mob.moveTo(summonTo.x, summonTo.y, summonTo.z)
        level.addFreshEntity(mob)

        return mob
    }

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

    fun getSummonableWitchCount(player: ServerPlayer): Int {
        return CovenPlayerAttachment.getData(player).covenWitchList.size
    }
}
