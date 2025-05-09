package dev.sterner.witchery.curse

import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfInsanity : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (level.gameTime % (20 * 60 + (level.random.nextDouble() * 30).toInt()) == 0L) {
            val pos = findLocationForInsanityMob(player.blockPosition(), level)
            if (pos != null) {
                val insanityEntity = WitcheryEntityTypes.INSANITY.get().create(level)
                insanityEntity?.moveTo(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                insanityEntity?.let { level.addFreshEntity(it) }
            }
        }


    }

    private fun findLocationForInsanityMob(center: BlockPos, level: Level): BlockPos? {
        val random = level.random

        val radiusX = 16
        val radiusY = 8
        val radiusZ = 16

        val positions = mutableListOf<BlockPos>()

        for (dx in -radiusX..radiusX) {
            for (dy in -radiusY..radiusY) {
                for (dz in -radiusZ..radiusZ) {
                    val pos = center.offset(dx, dy, dz)

                    val floor = level.getBlockState(pos)
                    val space1 = level.getBlockState(pos.above())
                    val space2 = level.getBlockState(pos.above(2))
                    val space3 = level.getBlockState(pos.above(3))

                    if (
                        floor.isSolid() &&
                        space1.isAir && space2.isAir && space3.isAir
                    ) {
                        positions.add(pos.above())
                    }
                }
            }
        }

        return if (positions.isNotEmpty()) positions[random.nextInt(positions.size)] else null
    }
}