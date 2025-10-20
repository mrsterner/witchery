package dev.sterner.witchery.features.spirit_world

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import java.util.*

object SleepingPlayerHandler {
    fun getPlayerFromSleeping(playerUUID: UUID, level: ServerLevel): SleepingLevelAttachment.PlayerSleepingData? {
        return SleepingLevelAttachment.getData(level).entries[playerUUID]
    }

    fun getPlayerFromSleepingUUID(sleepingUUID: UUID, level: ServerLevel): UUID? {
        val ret = SleepingLevelAttachment.getData(level).entries.entries.find { it.value.uuid == sleepingUUID }?.key
        return ret
    }

    fun add(playerUUID: UUID, sleepingUUID: UUID, pos: BlockPos, level: ServerLevel) {
        val oldData = SleepingLevelAttachment.getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries[playerUUID] = SleepingLevelAttachment.PlayerSleepingData(sleepingUUID, pos)
        SleepingLevelAttachment.setData(level, SleepingLevelAttachment.Data(updatedEntries))
    }

    fun remove(playerUUID: UUID, level: ServerLevel) {
        val oldData = SleepingLevelAttachment.getData(level)
        val updatedEntries = oldData.entries.toMutableMap()
        updatedEntries.remove(playerUUID)
        SleepingLevelAttachment.setData(level, SleepingLevelAttachment.Data(updatedEntries))
    }

    fun removeBySleepingUUID(sleepingUUID: UUID, level: ServerLevel) {
        val oldData = SleepingLevelAttachment.getData(level)
        val updatedEntries = oldData.entries.toMutableMap()

        val entryToRemove = updatedEntries.entries.find { it.value.uuid == sleepingUUID }?.key
        if (entryToRemove != null) {
            updatedEntries.remove(entryToRemove)
            SleepingLevelAttachment.setData(level, SleepingLevelAttachment.Data(updatedEntries))
        }
    }
}