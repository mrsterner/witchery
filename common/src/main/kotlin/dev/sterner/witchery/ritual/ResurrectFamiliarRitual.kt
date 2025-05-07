package dev.sterner.witchery.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.handler.FamiliarHandler
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class ResurrectFamiliarRitual : Ritual(Witchery.id("resurrect_familiar")) {

    override fun onEndRitual(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {

        if (level is ServerLevel) {
            val area = AABB.ofSize(blockPos.center, 11.0, 5.0, 11.0)

            val playersInArea = level.getEntitiesOfClass(Player::class.java, area)

            if (playersInArea.isNotEmpty()) {
                for (player in playersInArea) {
                    val bl = FamiliarHandler.resurrectDeadFamiliar(level, player.uuid, blockPos)
                    if (!bl) {
                        Containers.dropContents(level, blockPos, blockEntity)
                    }
                }
            }
        }
    }
}