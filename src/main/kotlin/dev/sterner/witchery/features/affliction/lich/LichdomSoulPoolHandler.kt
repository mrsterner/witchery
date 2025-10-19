package dev.sterner.witchery.features.affliction.lich


import dev.sterner.witchery.core.data_attachment.PhylacteryLevelDataAttachment
import dev.sterner.witchery.core.data_attachment.SoulPoolPlayerAttachment
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.AfflictionTypes
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.*

object LichdomSoulPoolHandler {

    fun increaseSouls(player: ServerPlayer, amount: Int, pos: BlockPos? = null): Int {
        if (amount <= 0) return 0
        val level = player.level() as? ServerLevel ?: return 0
        val playerUuid = player.uuid
        val data = PhylacteryLevelDataAttachment.getData(level)

        var remaining = amount

        if (pos != null) {
            val rec = data.phylacteries.find { it.owner == playerUuid && it.pos == pos && !it.hasSoul }
            if (rec != null) {
                PhylacteryLevelDataAttachment.setPhylacteryHasSoul(level, pos, true)
                remaining--
            }
        } else {
            val candidates = data.phylacteries.filter { it.owner == playerUuid && !it.hasSoul }.toMutableList()
            for (rec in candidates) {
                if (remaining <= 0) break
                PhylacteryLevelDataAttachment.setPhylacteryHasSoul(level, rec.pos, true)
                remaining--
            }
        }

        PhylacteryLevelDataAttachment.setData(level, data)

        val pool = SoulPoolPlayerAttachment.getData(player)
        val added = amount - remaining
        SoulPoolPlayerAttachment.setData(player, pool.copy(soulPool = pool.soulPool + added))

        return added
    }

    fun decreaseSouls(player: ServerPlayer, amount: Int, pos: BlockPos? = null): Int {
        if (amount <= 0) return 0
        val level = player.level() as? ServerLevel ?: return 0
        val playerUuid = player.uuid
        val data = PhylacteryLevelDataAttachment.getData(level)

        var remaining = amount

        if (pos != null) {
            val rec = data.phylacteries.find { it.owner == playerUuid && it.pos == pos && it.hasSoul }
            if (rec != null) {
                PhylacteryLevelDataAttachment.setPhylacteryHasSoul(level, pos, false)
                remaining--
            }
        } else {
            val filled = data.phylacteries.filter { it.owner == playerUuid && it.hasSoul }.toMutableList()
            for (rec in filled) {
                if (remaining <= 0) break
                PhylacteryLevelDataAttachment.setPhylacteryHasSoul(level, rec.pos, false)
                remaining--
            }
        }

        PhylacteryLevelDataAttachment.setData(level, data)

        val pool = SoulPoolPlayerAttachment.getData(player)
        val removed = amount - remaining
        SoulPoolPlayerAttachment.setData(player, pool.copy(soulPool = pool.soulPool - removed))

        return removed
    }


    fun getMaxSouls(player: ServerPlayer): Int {
        val level = player.level() as? ServerLevel ?: return 0
        val playerUuid = player.uuid
        return PhylacteryLevelDataAttachment.listPhylacteriesForPlayer(level, playerUuid).size
    }

    fun getCurrentSouls(player: ServerPlayer): Int {
        val level = player.level() as? ServerLevel ?: return 0
        val playerUuid = player.uuid
        return PhylacteryLevelDataAttachment.listPhylacteriesForPlayer(level, playerUuid).count { it.hasSoul }
    }

    fun destroyPhylactery(level: ServerLevel, pos: BlockPos) {
        val data = PhylacteryLevelDataAttachment.getData(level)
        val rec = data.phylacteries.find { it.pos == pos } ?: run {
            PhylacteryLevelDataAttachment.queueRemovePhylactery(level, pos)
            return
        }

        if (rec.hasSoul) {
            val ownerPlayer = level.getPlayerByUUID(rec.owner)
            if (ownerPlayer != null && ownerPlayer is ServerPlayer) {
                PhylacteryLevelDataAttachment.setPhylacteryHasSoul(level, pos, false)
                AfflictionPlayerAttachment.smartUpdate(ownerPlayer) {
                    withPhylacterySouls(getPhylacterySouls(ownerPlayer) - 1)
                }
            } else {
                PhylacteryLevelDataAttachment.addPendingPlayerDelta(level, rec.owner, -1)
                data.pendingPhylacteryChanges.add(
                    PhylacteryLevelDataAttachment.PendingPhylacteryChange(pos, "remove", Optional.empty(), false)
                )
                PhylacteryLevelDataAttachment.setData(level, data)
            }
        } else {
            PhylacteryLevelDataAttachment.removePhylactery(level, pos)
        }
    }

    private fun getPhylacterySouls(player: ServerPlayer): Int {
        return getCurrentSouls(player)
    }

    fun setMaxSouls(entity: Player, lichLevel: Int) {
        val maxSouls = when {
            lichLevel in 2..3 -> 1
            lichLevel in 4..5 -> 2
            lichLevel >= 6 -> 3
            else -> 0
        }

        val data = SoulPoolPlayerAttachment.getData(entity)
        val clampedSouls = data.soulPool.coerceAtMost(maxSouls)
        SoulPoolPlayerAttachment.setData(entity, data.copy(maxSouls = maxSouls, soulPool = clampedSouls))
    }

    fun updateLichHunger(player: Player) {
        val lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
        if (lichLevel < 2) return

        val data = SoulPoolPlayerAttachment.getData(player)

        if (data.soulPool > 0) {
            player.foodData.foodLevel = 20
            player.foodData.setSaturation(5.0f)
        } else {
            player.foodData.foodLevel = 1
            player.foodData.setSaturation(0.0f)
        }
    }
}