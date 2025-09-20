package dev.sterner.witchery.block.dream_weaver


import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState

class DreamWeaverBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.DREAM_WEAVER.get(), blockPos, blockState) {

    fun applyWakeUpEffect(player: Player, corruptCount: Int) {
        if (blockState.`is`(WitcheryBlocks.DREAM_WEAVER_OF_FLEET_FOOT.get())) {
            if (corruptCount > 0) {
                player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60 * 20 * 5))
            } else {
                player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20 * 5))
            }
        }

        if (blockState.`is`(WitcheryBlocks.DREAM_WEAVER_OF_FASTING.get())) {
            if (corruptCount > 0) {
                player.addEffect(MobEffectInstance(MobEffects.HUNGER, 60 * 20 * 5))
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 60 * 20 * 5))
            } else {
                player.addEffect(MobEffectInstance(MobEffects.SATURATION, 60 * 20 * 5))
            }
        }

        if (blockState.`is`(WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get())) {
            if (corruptCount > 0) {
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 60 * 20 * 1))
                if (corruptCount > 1) {
                    player.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 60 * 20 * 1))
                }

            }
        }

        if (blockState.`is`(WitcheryBlocks.DREAM_WEAVER_OF_IRON_ARM.get())) {
            if (corruptCount > 0) {
                player.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60 * 20 * 5))
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 60 * 20 * 5))
            } else {
                player.addEffect(MobEffectInstance(MobEffects.DIG_SPEED, 60 * 20 * 5))
            }
        }
    }
}