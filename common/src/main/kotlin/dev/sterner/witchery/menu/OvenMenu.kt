package dev.sterner.witchery.menu

import dev.sterner.witchery.block.oven.OvenBlockEntity
import dev.sterner.witchery.menu.slot.OvenFuelSlot
import dev.sterner.witchery.registry.WitcheryMenuTypes
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity

class OvenMenu(id: Int, val inventory: Inventory, val buf: FriendlyByteBuf) : AbstractContainerMenu(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), id) {

    private var data: ContainerData = SimpleContainerData(4)
    private var level: Level = inventory.player.level()
    private var blockEntity: OvenBlockEntity? = null

    init {
        val blockPos = buf.readBlockPos()
        if (level.getBlockEntity(blockPos) is OvenBlockEntity) {
            blockEntity = level.getBlockEntity(blockPos) as OvenBlockEntity
            data = blockEntity!!.dataAccess
        }

        this.addSlot(Slot(blockEntity!!, INGREDIENT_SLOT, 56, 17))
        this.addSlot(OvenFuelSlot(this, blockEntity, FUEL_SLOT, 56, 53))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, RESULT_SLOT, 116, 35))
        
        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (i in 0..8) {
            this.addSlot(Slot(inventory, i, 8 + i * 18, 142))
        }

        this.addDataSlots(data)
    }

    override fun stillValid(player: Player): Boolean {
        return this.blockEntity!!.stillValid(player)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasItem()) {
            val itemStack2 = slot.item
            itemStack = itemStack2.copy()
            if (index == 2) {
                if (!this.moveItemStackTo(itemStack2, 3, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY
                }

                slot.onQuickCraft(itemStack2, itemStack)
            } else if (index != 1 && index != 0) {
                if (this.canSmelt(itemStack2)) {
                    if (!this.moveItemStackTo(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (this.isFuel(itemStack2)) {
                    if (!this.moveItemStackTo(itemStack2, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 3..<USE_ROW_SLOT_START) {
                    if (!this.moveItemStackTo(itemStack2, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in USE_ROW_SLOT_START..<USE_ROW_SLOT_END && !this.moveItemStackTo(itemStack2, 3, USE_ROW_SLOT_START, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemStack2, 3, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack2.isEmpty) {
                slot.setByPlayer(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, itemStack2)
        }

        return itemStack
    }

    fun canSmelt(stack: ItemStack?): Boolean {
        return level.recipeManager.getRecipeFor(
            WitcheryRecipeTypes.OVEN_RECIPE_TYPE.get(), SingleRecipeInput(stack),
            this.level
        ).isPresent
    }

    fun isFuel(stack: ItemStack?): Boolean {
        return AbstractFurnaceBlockEntity.isFuel(stack)
    }

    fun getBurnProgress(): Float {
        val i = data[2]
        val j = data[3]
        return if (j != 0 && i != 0) Mth.clamp(i.toFloat() / j.toFloat(), 0.0f, 1.0f) else 0.0f
    }

    fun getLitProgress(): Float {
        var i = data[1]
        if (i == 0) {
            i = 200
        }

        return Mth.clamp(data[0].toFloat() / i.toFloat(), 0.0f, 1.0f)
    }

    fun isLit(): Boolean {
        return data[0] > 0
    }


    companion object {
        const val INGREDIENT_SLOT: Int = 0
        const val FUEL_SLOT: Int = 1
        const val RESULT_SLOT: Int = 2

        const val INV_SLOT_START: Int = 3
        const val USE_ROW_SLOT_START: Int = 30
        const val USE_ROW_SLOT_END: Int = 39
    }
}