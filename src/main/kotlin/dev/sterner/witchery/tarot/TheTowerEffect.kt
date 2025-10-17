package dev.sterner.witchery.tarot

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class TheTowerEffect : TarotEffect(17) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Tower (Reversed)" else "The Tower"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Fear of change" else "Sudden upheaval"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed && player.level() is ServerLevel) {
            val level = player.level() as ServerLevel

            if (level.random.nextFloat() < 0.001f) {
                val babaYaga = WitcheryEntityTypes.BABA_YAGA.get().create(level)
                babaYaga?.let {
                    var spawnX: Double
                    var spawnZ: Double
                    var attempts = 0

                    do {
                        val angle = level.random.nextDouble() * 2 * Math.PI
                        val distance = 8.0 + level.random.nextDouble() * 8.0

                        spawnX = player.x + Math.cos(angle) * distance
                        spawnZ = player.z + Math.sin(angle) * distance

                        attempts++
                    } while (attempts < 10 && player.distanceToSqr(spawnX, player.y, spawnZ) < 64.0)

                    it.moveTo(spawnX, player.y, spawnZ, 0f, 0f)
                    level.addFreshEntity(it)

                    player.displayClientMessage(
                        Component.literal("The Tower crumbles! Baba Yaga approaches!")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                        false
                    )

                    level.playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.WITHER_SPAWN,
                        SoundSource.HOSTILE,
                        1.0f,
                        0.8f
                    )
                    removeCardFromReading(player, this.cardNumber)
                }
            }
        }
    }

    override fun onBlockBreak(player: Player, blockState: BlockState, pos: BlockPos, isReversed: Boolean) {
        if (!isReversed && player.level().random.nextFloat() < 0.05f) {
            player.level().explode(
                null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                1.5f, Level.ExplosionInteraction.NONE
            )
        }
    }
}