package dev.sterner.witchery.fetish

import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.block.effigy.EffigyState
import dev.sterner.witchery.data_attachment.MiscPlayerAttachment
import dev.sterner.witchery.entity.DeathEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.core.particles.ParticleTypes
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

        var playerDeathOnline: Player? = null

        for (player in level.players()) {
            val data = MiscPlayerAttachment.getData(player)
            if (data.isDeath) {
                playerDeathOnline = player
                break
            }
        }
        if (playerDeathOnline != null) {
            playerDeathOnline.teleportTo(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5)
        } else {
            val deathEntity = DeathEntity(level)

            deathEntity.moveTo(
                Vec3(
                    pos.x + 0.5,
                    pos.y + 1.0,
                    pos.z + 0.5
                )
            )

            level.addFreshEntity(deathEntity)
        }

        level.playSound(
            null,
            pos,
            SoundEvents.WITHER_SPAWN,
            SoundSource.HOSTILE,
            0.6f,
            0.5f
        )

        for (i in 0 until 20) {
            val d0 = pos.x + 0.5 + (level.random.nextDouble() - 0.5) * 2.0
            val d1 = pos.y + 0.5 + (level.random.nextDouble() - 0.5) * 2.0
            val d2 = pos.z + 0.5 + (level.random.nextDouble() - 0.5) * 2.0

            level.addParticle(
                ParticleTypes.SMOKE,
                d0,
                d1,
                d2,
                0.0,
                0.0,
                0.0
            )
        }

        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
    }
}