package dev.sterner.witchery.features.tarot

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class TheWorldEffect : TarotEffect(22) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The World (Reversed)" else "The World"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Incomplete and scattered" else "Everything in harmony"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            val effects = listOf(
                MobEffects.MOVEMENT_SPEED to 0,
                MobEffects.DIG_SPEED to 0,
                MobEffects.LUCK to 0,
                MobEffects.REGENERATION to 0
            )

            for ((effect, level) in effects) {
                if (!player.hasEffect(effect)) {
                    player.addEffect(MobEffectInstance(effect, 400, level, true, false))
                }
            }
        } else {
            if (player.level().gameTime % 600 == 0L) {
                val debuffs = listOf(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    MobEffects.DIG_SLOWDOWN,
                    MobEffects.WEAKNESS,
                    MobEffects.UNLUCK
                )
                player.addEffect(MobEffectInstance(debuffs.random(), 200, 0))
            }
        }
    }

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.giveExperiencePoints(10)
        }
    }

    override fun onBlockBreak(player: Player, blockState: BlockState, pos: BlockPos, isReversed: Boolean) {
        if (!isReversed && player.level().random.nextFloat() < 0.1f) {
            Block.popResource(player.level(), pos, ItemStack(blockState.block.asItem()))
        }
    }
}