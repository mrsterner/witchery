package dev.sterner.witchery.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.entity.OwlEntity
import dev.sterner.witchery.platform.FamiliarLevelAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
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
                val area = AABB.ofSize(blockPos.center, 11.0, 5.0, 11.0)

                // Find entities that are not already bound
                val unboundEntities = level.getEntitiesOfClass(LivingEntity::class.java, area)
                    .filter { (it is Cat || it is Frog || it is OwlEntity) && !FamiliarLevelAttachment.isBound(level, it) }

                val playersInArea = level.getEntitiesOfClass(Player::class.java, area)

                if (playersInArea.isNotEmpty() && unboundEntities.isNotEmpty()) {
                    val player = playersInArea[0]
                    val animal = unboundEntities[0]

                    FamiliarLevelAttachment.bindOwnerAndFamiliar(level, player.uuid, animal)

                    for (i in 0..10) {
                        val offsetX = (level.random.nextDouble() - 0.5) * 0.5
                        val offsetY = (level.random.nextDouble() - 0.5) * 0.5 + 0.5
                        val offsetZ = (level.random.nextDouble() - 0.5) * 0.5
                        level.sendParticles(
                            ParticleTypes.END_ROD,
                            animal.x + offsetX,
                            animal.y + offsetY,
                            animal.z + offsetZ,
                            1,
                            0.0, 0.0, 0.0,
                            0.1
                        )
                    }
                } else {
                    Containers.dropContents(level, blockPos, blockEntity)
                }
            }
        }
    }
}