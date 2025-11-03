package dev.sterner.witchery.features.fetish

import dev.sterner.witchery.core.api.FetishEffect
import dev.sterner.witchery.content.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.content.block.effigy.EffigyState
import dev.sterner.witchery.content.entity.DeathEntity
import dev.sterner.witchery.features.death.DeathTransformationHelper
import dev.sterner.witchery.features.misc.MiscPlayerAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3

class SummonDeathFetishEffect : FetishEffect() {

    override fun onTickEffect(
        level: Level,
        blockEntity: EffigyBlockEntity,
        state: EffigyState?,
        pos: BlockPos,
        taglock: NonNullList<ItemStack>,
        tickRate: Int
    ) {
        if (level.isClientSide) return

        val serverLevel = level as ServerLevel
        val summonPos = Vec3(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5)

        val deathPlayer = DeathTransformationHelper.findDeathPlayer(level)

        if (deathPlayer != null) {
            teleportDeathPlayer(deathPlayer, summonPos, serverLevel, pos)
        } else {
            summonDeathEntity(summonPos, serverLevel, pos)
        }

        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
    }

    private fun teleportDeathPlayer(
        player: Player,
        summonPos: Vec3,
        level: ServerLevel,
        pos: BlockPos
    ) {
        level.sendParticles(
            ParticleTypes.PORTAL,
            player.x, player.y + player.bbHeight / 2, player.z,
            50, 0.5, 1.0, 0.5, 0.5
        )

        level.sendParticles(
            ParticleTypes.SOUL_FIRE_FLAME,
            player.x, player.y + player.bbHeight / 2, player.z,
            30, 0.5, 0.5, 0.5, 0.1
        )

        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.ENDERMAN_TELEPORT,
            SoundSource.PLAYERS,
            1.0f,
            0.8f
        )

        player.teleportTo(summonPos.x, summonPos.y, summonPos.z)

        level.sendParticles(
            ParticleTypes.SOUL_FIRE_FLAME,
            summonPos.x, summonPos.y + 1, summonPos.z,
            50, 0.5, 1.0, 0.5, 0.1
        )

        level.sendParticles(
            ParticleTypes.LARGE_SMOKE,
            summonPos.x, summonPos.y, summonPos.z,
            30, 0.5, 0.5, 0.5, 0.05
        )

        level.playSound(
            null,
            pos,
            SoundEvents.WITHER_SPAWN,
            SoundSource.HOSTILE,
            1.5f,
            0.5f
        )

        val data = MiscPlayerAttachment.getData(player)
        data.hasDeathTeleport = true
        MiscPlayerAttachment.setData(player, data)
    }

    private fun summonDeathEntity(
        summonPos: Vec3,
        level: ServerLevel,
        pos: BlockPos
    ) {
        val deathEntity = DeathEntity(level)
        deathEntity.moveTo(summonPos)

        level.addFreshEntity(deathEntity)

        level.sendParticles(
            ParticleTypes.SOUL_FIRE_FLAME,
            summonPos.x, summonPos.y + 1, summonPos.z,
            50, 0.5, 1.0, 0.5, 0.1
        )

        level.sendParticles(
            ParticleTypes.LARGE_SMOKE,
            summonPos.x, summonPos.y, summonPos.z,
            30, 0.5, 0.5, 0.5, 0.05
        )

        for (i in 0 until 40) {
            val angle = (i / 40.0) * Math.PI * 2
            val radius = 2.0
            val px = summonPos.x + kotlin.math.cos(angle) * radius
            val pz = summonPos.z + kotlin.math.sin(angle) * radius

            level.sendParticles(
                ParticleTypes.PORTAL,
                px, summonPos.y + 0.1, pz,
                1, 0.0, 0.5, 0.0, 0.1
            )
        }

        level.playSound(
            null,
            pos,
            SoundEvents.WITHER_SPAWN,
            SoundSource.HOSTILE,
            1.5f,
            0.5f
        )
    }
}