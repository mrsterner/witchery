package dev.sterner.witchery.data_attachment.possession

import com.mojang.serialization.Codec
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import kotlin.experimental.and


class OrderedInventory(size: Int) : SimpleContainer(size) {
    public override fun fromTag(tags: ListTag, registries: HolderLookup.Provider) {
        for (slot in 0..<this.containerSize) {
            this.setItem(slot, ItemStack.EMPTY)
        }

        for (i in 0..<tags.size) {
            val compoundTag: CompoundTag = tags.getCompound(i)
            val slot: Int = (compoundTag.getByte("Slot") and 255.toByte()).toInt()
            if (slot >= 0 && slot < this.containerSize) {
                this.setItem(slot, ItemStack.parse(registries, compoundTag).get())
            }
        }
    }

    public override fun createTag(registries: HolderLookup.Provider): ListTag {
        val listTag: ListTag = ListTag()

        for (slot in 0..<this.containerSize) {
            val itemStack: ItemStack = this.getItem(slot)
            if (!itemStack.isEmpty) {
                val compoundTag: CompoundTag = CompoundTag()
                compoundTag.putByte("Slot", slot.toByte())
                itemStack.save(registries, compoundTag)
                listTag.add(compoundTag)
            }
        }

        return listTag
    }
}