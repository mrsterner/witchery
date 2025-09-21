package dev.sterner.witchery.api.multiblock

import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState


open class MultiBlockComponentBlockEntity(
    blockPos: BlockPos, blockState: BlockState
) : WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.MULTI_BLOCK_COMPONENT.get(), blockPos, blockState) {
    var corePos: BlockPos? = null


    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        corePos = if (tag.contains("X")) BlockPos(
            tag.getInt("X"),
            tag.getInt("Y"),
            tag.getInt("Z")
        ) else null
        super.loadAdditional(tag, registries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        if (corePos != null) {
            tag.putInt("X", corePos!!.x)
            tag.putInt("Y", corePos!!.y)
            tag.putInt("Z", corePos!!.z)
        }
        super.saveAdditional(tag, registries)
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        val core = corePos?.let { level?.getBlockEntity(it) }
        if (corePos != null && core is MultiBlockCoreEntity) {
            return core.onUseWithoutItem(pPlayer)
        }
        return super.onUseWithoutItem(pPlayer)
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        val core = corePos?.let { level?.getBlockEntity(it) }
        if (corePos != null && core is MultiBlockCoreEntity) {
            return core.onUseWithItem(pPlayer, pStack, pHand)
        }
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun onBreak(player: Player) {
        val core = corePos?.let { level?.getBlockEntity(it) }
        if (core is MultiBlockCoreEntity) {
            core.onBreak(player)
        }
        super.onBreak(player)
    }
}