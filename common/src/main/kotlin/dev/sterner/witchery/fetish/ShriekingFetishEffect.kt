package dev.sterner.witchery.fetish

import dev.sterner.witchery.api.FetishEffect
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class ShriekingFetishEffect : FetishEffect() {

    private fun shriek(level: Level, pos: Vec3){
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GHAST_SCREAM, SoundSource.BLOCKS)
    }

    override fun onUnknownPlayerNearbyTick(level: Level, pos: BlockPos, nearby: List<Player>) {
        if (level.random.nextDouble() < 0.1) {
            shriek(level, pos.center)
        }
    }
}