package dev.sterner.witchery.content.block

import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CaveVines
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent

interface Lifeblood {

    companion object {
        fun use(entity: Entity?, state: BlockState, level: Level, pos: BlockPos): InteractionResult {
            if (state.hasProperty(CaveVines.BERRIES) && state.getValue(CaveVines.BERRIES)) {
                Block.popResource(level, pos, ItemStack(WitcheryItems.LIFEBLOOD_BERRY.get(), 1))
                val pitch = Mth.randomBetween(level.random, 0.8f, 1.2f)
                level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, pitch)
                val blockState = state.setValue(CaveVines.BERRIES, false)
                level.setBlock(pos, blockState, 2)
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockState))
                return InteractionResult.sidedSuccess(level.isClientSide)
            } else {
                return InteractionResult.PASS
            }
        }
    }
}