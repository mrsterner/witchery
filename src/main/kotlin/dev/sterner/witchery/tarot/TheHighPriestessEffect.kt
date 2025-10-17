package dev.sterner.witchery.tarot

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState

class TheHighPriestessEffect : TarotEffect(3) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The High Priestess (Reversed)" else "The High Priestess"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Secrets hidden from you" else "See what others cannot"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false))
            }
        }
    }

    override fun onBlockBreak(player: Player, blockState: BlockState, pos: BlockPos, isReversed: Boolean) {
        if (!isReversed && player.level().random.nextFloat() < 0.05f) {
            //TODO make a render
            val level = player.level()
            if (level is ServerLevel) {
                BlockPos.betweenClosedStream(pos.offset(-8, -8, -8), pos.offset(8, 8, 8))
                    .forEach { checkPos ->
                        val state = level.getBlockState(checkPos)
                        if (state.`is`(BlockTags.COAL_ORES) ||
                            state.`is`(BlockTags.IRON_ORES) ||
                            state.`is`(BlockTags.GOLD_ORES) ||
                            state.`is`(BlockTags.DIAMOND_ORES)) {
                            level.sendParticles(
                                ParticleTypes.WAX_ON,
                                checkPos.x + 0.5,
                                checkPos.y + 0.5,
                                checkPos.z + 0.5,
                                3, 0.3, 0.3, 0.3, 0.0
                            )
                        }
                    }
            }
        }
    }
}