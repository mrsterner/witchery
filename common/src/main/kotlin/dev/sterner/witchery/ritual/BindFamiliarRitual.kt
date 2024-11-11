package dev.sterner.witchery.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.entity.OwlEntity
import dev.sterner.witchery.platform.FamiliarLevelAttachment
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.frog.Frog
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class BindFamiliarRitual : Ritual(Witchery.id("bind_familiar")) {

    companion object {

        fun onEndRitual(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {
            if (level is ServerLevel) {
                val area = AABB.ofSize(blockPos.center, 11.0,5.0,11.0)

                val entitiesInArea = level.getEntitiesOfClass(LivingEntity::class.java, area).filter { it is Cat || it is Frog || it is OwlEntity }
                val playersInArea = level.getEntitiesOfClass(Player::class.java, area)

                if (playersInArea.isNotEmpty() && entitiesInArea.isNotEmpty()) {
                    val player = playersInArea[0]
                    val animal = entitiesInArea[0]

                    FamiliarLevelAttachment.bindOwnerAndFamiliar(level, player.uuid, animal)
                    return
                }
            }
        }
    }
}