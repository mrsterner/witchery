package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.data_attachment.CursePlayerAttachment
import dev.sterner.witchery.features.curse.CurseHandler
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
                    val curse = WitcheryCurseRegistry.CURSES_REGISTRY.get(oldestCurse.curseId)
                    if (curse != null) {
                        CurseHandler.removeCurse(player, curse)
                    }
                }
            }
        }
    }
}