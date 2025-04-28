package dev.sterner.witchery.handler

import dev.sterner.witchery.platform.SleepingLevelAttachment.Data
import dev.sterner.witchery.platform.SleepingLevelAttachment.PlayerSleepingData
import dev.sterner.witchery.platform.SleepingLevelAttachment.getData
import dev.sterner.witchery.platform.SleepingLevelAttachment.setData
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import java.util.*

object SleepingPlayerHandler {
    fun getPlayerFromSleeping(playerUUID: UUID, level: ServerLevel): PlayerSleepingData? {
        return getData(level).entries[playerUUID]
    }

    fun getPlayerFromSleepingUUID(sleepingUUID: UUID, level: ServerLevel): UUID? {
        val ret = getData(level).entries.entries.find { it.value.uuid == sleepingUUID }?.key
        return ret
    }

    fun add(playerUUID: UUID, sleepingUUID: UUID, pos: BlockPos, level: ServerLevel) {
        val oldData = getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries[playerUUID] = PlayerSleepingData(sleepingUUID, pos)
        setData(level, Data(updatedEntries))
    }

    fun remove(playerUUID: UUID, level: ServerLevel) {
        val oldData = getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries.remove(playerUUID)
        setData(level, Data(updatedEntries))
    }

    fun removeBySleepingUUID(sleepingUUID: UUID, level: ServerLevel) {
        val oldData = getData(level)
        val updatedEntries = oldData.entries.toMutableMap()

        val entryToRemove = updatedEntries.entries.find { it.value.uuid == sleepingUUID }?.key
        if (entryToRemove != null) {
            updatedEntries.remove(entryToRemove)
            setData(level, Data(updatedEntries))
        }
    }
}