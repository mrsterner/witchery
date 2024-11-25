package dev.sterner.witchery.block.effigy

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class EffigyBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.EFFIGY.get().create(pos, state)
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is EffigyBlockEntity) {
            if (!level.isClientSide) {
                val itemStack = blockEntity.getItemStack()

                itemStack.set(WitcheryDataComponents.BANSHEE_COUNT.get(), blockEntity.bansheeCount)
                itemStack.set(WitcheryDataComponents.SPECTRE_COUNT.get(), blockEntity.specterCount)
                itemStack.set(WitcheryDataComponents.POLTERGEIST_COUNT.get(), blockEntity.poltergeistCount)

                val itemEntity = ItemEntity(level, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, itemStack)
                itemEntity.setDefaultPickUpDelay()
                level.addFreshEntity(itemEntity)
            }
        }

        return super.playerWillDestroy(level, pos, state, player)
    }
}