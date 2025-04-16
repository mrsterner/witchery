package dev.sterner.witchery.block.bear_trap

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class BearTrapBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.BEAR_TRAP.get(), blockPos, blockState) {

    var isUsing = false
    var isUsingTicker = 0

    var angle = 0
    var prevAngle = 0

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        isUsing = true
        isUsingTicker = 10
        setChanged()
        return super.onUseWithoutItem(pPlayer)
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        prevAngle = angle

        if (isUsingTicker > 0) {
            isUsingTicker--
            if (angle < 80) {
                angle = minOf(angle + 5, 80)
            }
            setChanged()
        } else {
            if (angle > 0) {
                angle = maxOf(angle - 5, 0)
                setChanged()
            } else {
                isUsing = false
            }
        }
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        angle = compoundTag.getInt("angle")
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putInt("angle", angle)
    }
}