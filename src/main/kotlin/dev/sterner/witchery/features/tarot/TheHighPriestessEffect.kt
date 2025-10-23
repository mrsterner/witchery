package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.network.HighlightOresS2CPayload
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.network.PacketDistributor

class TheHighPriestessEffect : TarotEffect(3) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_high_priestess.reversed" else "tarot.witchery.the_high_priestess"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_high_priestess.reversed.description" else "tarot.witchery.the_high_priestess.description"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 11, 0, true, false))
        }
    }

    override fun onBlockBreak(player: Player, blockState: BlockState, pos: BlockPos, isReversed: Boolean) {
        if (!isReversed && player.level().random.nextFloat() < 0.05f) {
            val level = player.level()
            if (level is ServerLevel) {
                val orePositions = mutableListOf<BlockPos>()

                BlockPos.betweenClosedStream(pos.offset(-8, -8, -8), pos.offset(8, 8, 8))
                    .forEach { checkPos ->
                        val state = level.getBlockState(checkPos)
                        if (state.`is`(BlockTags.COAL_ORES) ||
                            state.`is`(BlockTags.IRON_ORES) ||
                            state.`is`(BlockTags.COPPER_ORES) ||
                            state.`is`(BlockTags.GOLD_ORES) ||
                            state.`is`(BlockTags.REDSTONE_ORES) ||
                            state.`is`(BlockTags.LAPIS_ORES) ||
                            state.`is`(BlockTags.EMERALD_ORES) ||
                            state.`is`(BlockTags.DIAMOND_ORES)) {

                            orePositions.add(checkPos.immutable())

                            level.sendParticles(
                                ParticleTypes.WAX_ON,
                                checkPos.x + 0.5,
                                checkPos.y + 0.5,
                                checkPos.z + 0.5,
                                5, 0.3, 0.3, 0.3, 0.0
                            )
                        }
                    }

                if (player is ServerPlayer && orePositions.isNotEmpty()) {
                    PacketDistributor.sendToPlayer(
                        player,
                        HighlightOresS2CPayload(orePositions, 200)
                    )
                }
            }
        } else if (isReversed) {
            if (player.level().random.nextFloat() < 0.15f) {
                player.giveExperiencePoints(-1)
            }
        }
    }
}