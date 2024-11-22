package dev.sterner.witchery.block.grassper

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

class GrassperBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.GRASSPER.get(), blockPos, blockState),
    Container, WorldlyContainer {

    var item = NonNullList.withSize(1, ItemStack.EMPTY)

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        if (item[0].isEmpty) {
            item[0] = pStack.copy()
            item[0].count = 1
            pStack.shrink(1)
            setChanged()
            return ItemInteractionResult.SUCCESS
        } else {
            Containers.dropContents(level, blockPos, this)
        }
        setChanged()

        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun onBreak(player: Player) {
        Containers.dropContents(level, blockPos, this)
        super.onBreak(player)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        this.item = NonNullList.withSize(1, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.item, pRegistries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, this.item, registries)
    }

    override fun clearContent() {
        item.clear()
    }

    override fun getContainerSize(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
       return item.isEmpty()
    }

    override fun getItem(slot: Int): ItemStack {
        return item[slot]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(this.item, slot, amount)
        if (!itemStack.isEmpty) {
            this.setChanged()
        }

        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return ContainerHelper.takeItem(this.item, slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        item[slot] = stack
        stack.limitSize(this.getMaxStackSize(stack))
        this.setChanged()
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun getSlotsForFace(side: Direction): IntArray {
        return (0..<item.size).toList().toIntArray()
    }

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean {
        return true
    }

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean {
        return true
    }
}