package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.content.entity.OwlEntity
import dev.sterner.witchery.features.familiar.FamiliarHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Ocelot
import net.minecraft.world.entity.animal.frog.Frog
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class BindFamiliarRitual : Ritual(Witchery.id("bind_familiar")) {

    override fun onEndRitual(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {
        if (level is ServerLevel) {
            val area = AABB.ofSize(blockPos.center, 11.0, 5.0, 11.0)

            val unboundEntities = level.getEntitiesOfClass(LivingEntity::class.java, area)
                .filter { (it is Cat || it is Frog || it is OwlEntity) && !FamiliarHandler.isBound(level, it) }

            val playersInArea = level.getEntitiesOfClass(Player::class.java, area)

            if (playersInArea.isNotEmpty() && unboundEntities.isNotEmpty()) {
                val player = playersInArea[0]
                val animal = unboundEntities[0]

                FamiliarHandler.bindOwnerAndFamiliar(level, player.uuid, animal)
                WitcheryApi.makePlayerWitchy(player)
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