package dev.sterner.witchery.ritual

import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.data_attachment.CursePlayerAttachment
import dev.sterner.witchery.handler.CurseHandler
import dev.sterner.witchery.registry.WitcheryCurseRegistry
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

class RemoveCurseRitual : Ritual("remove_curse") {

    override fun onEndRitual(level: Level, blockPos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {
        if (goldenChalkBlockEntity.targetPlayer != null) {
            val playerUUID = goldenChalkBlockEntity.targetPlayer
            if (playerUUID?.let { level.getPlayerByUUID(it) } != null) {
                val player = level.getPlayerByUUID(playerUUID)!!
                val curses: MutableList<CursePlayerAttachment.PlayerCurseData> =
                    CursePlayerAttachment.getData(player).playerCurseList
                val oldestCurse = curses.minByOrNull { it.duration }
                if (oldestCurse != null) {
                    val curse = WitcheryCurseRegistry.CURSES.registry.get().get(oldestCurse.curseId)
                    if (curse != null) {
                        CurseHandler.removeCurse(player, curse)
                    }
                }
            }
        }
    }
}