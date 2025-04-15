package dev.sterner.witchery.block.sacrificial_circle

import dev.architectury.event.EventResult
import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.CandleBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class SacrificialBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(
        WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get(),
        SacrificialBlock.STRUCTURE.get(),
        blockPos,
        blockState
    ),
    Container {

    var hasSkull: Boolean = true
    var candles = NonNullList.withSize(8, ItemStack.EMPTY)

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        if (pStack.item is BlockItem) {
            val item = pStack.item as BlockItem
            if (item.block is CandleBlock) {
                for (i in candles.indices) {
                    if (candles[i].isEmpty) {
                        candles[i] = pStack.copy()
                        candles[i].count = 1
                        pStack.shrink(1)
                        level?.setBlockAndUpdate(
                            blockPos,
                            blockState.setValue(BlockStateProperties.LIT, candles.isNotEmpty())
                        )
                        setChanged()
                        return ItemInteractionResult.SUCCESS
                    }
                }
            }
        }

        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun onBreak(player: Player) {
        if (!player.isCreative) {
            level?.let { Containers.dropContents(it, blockPos, candles) }
            if (hasSkull) {
                level?.let {
                    Containers.dropItemStack(
                        it,
                        blockPos.x + 0.5,
                        blockPos.y + 0.5,
                        blockPos.z + 0.5,
                        Items.SKELETON_SKULL.defaultInstance
                    )
                }
            }
        }
        super.onBreak(player)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        this.candles = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.candles, pRegistries)
        this.hasSkull = pTag.getBoolean("hasSkull")
        super.loadAdditional(pTag, pRegistries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        ContainerHelper.saveAllItems(tag, this.candles, registries)
        tag.putBoolean("hasSkull", this.hasSkull)
        super.saveAdditional(tag, registries)
    }

    override fun isEmpty(): Boolean {
        for (itemStack in this.candles) {
            if (!itemStack.isEmpty) {
                return false
            }
        }

        return true
    }

    override fun getItem(slot: Int): ItemStack {
        return this.candles[slot]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(this.candles, slot, amount)
        if (!itemStack.isEmpty) {
            this.setChanged()
        }

        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return ContainerHelper.takeItem(this.candles, slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        candles[slot] = stack
        stack.limitSize(this.getMaxStackSize(stack))
        this.setChanged()
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun clearContent() {
        this.candles.clear()
    }

    override fun getContainerSize(): Int {
        return candles.size
    }

    companion object {
        fun rightClick(
            player: Player?,
            interactionHand: InteractionHand?,
            blockPos: BlockPos,
            direction: Direction?
        ): EventResult? {
            if (player != null && player.mainHandItem.`is`(Items.SKELETON_SKULL)) {
                if (player.level().getBlockEntity(blockPos) is SacrificialBlockEntity) {
                    val be = player.level().getBlockEntity(blockPos) as SacrificialBlockEntity
                    be.hasSkull = true
                    be.setChanged()
                    player.mainHandItem.shrink(1)
                    return EventResult.interruptTrue()
                }
            }

            return EventResult.pass()
        }
    }
}