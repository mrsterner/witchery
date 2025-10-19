package dev.sterner.witchery.content.block.bear_trap


import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class BearTrapBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.BEAR_TRAP.get(), blockPos, blockState) {

    var isOpen = false
    private var isTriggered = false

    var angle = 0
    var prevAngle = 0

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (!isOpen && !isTriggered) {
            isOpen = true
            setChanged()
        }
        return super.onUseWithoutItem(pPlayer)
    }

    fun triggerBearTrap() {
        if (isOpen && !isTriggered) {
            isTriggered = true
            setChanged()
        }
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        prevAngle = angle

        when {
            isOpen && !isTriggered && angle < 110 -> {
                angle = minOf(angle + 2, 110)
                setChanged()
            }

            isTriggered && angle > 0 -> {
                angle = maxOf(angle - 30, 0)
                setChanged()
            }

            isTriggered && angle <= 0 -> {
                isOpen = false
                isTriggered = false
                setChanged()
            }
        }
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        angle = compoundTag.getInt("angle")
        isOpen = compoundTag.getBoolean("isOpen")
        isTriggered = compoundTag.getBoolean("isTriggered")
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putInt("angle", angle)
        compoundTag.putBoolean("isOpen", isOpen)
        compoundTag.putBoolean("isTriggered", isTriggered)
    }
}
