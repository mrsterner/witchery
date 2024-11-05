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

class OvenMenu(id: Int, inventory: Inventory, buf: FriendlyByteBuf) :
    AbstractContainerMenu(WitcheryMenuTypes.OVEN_MENU_TYPE.get(), id) {

    private var data: ContainerData = SimpleContainerData(4)
    private var level: Level = inventory.player.level()
    private var blockEntity: OvenBlockEntity? = null

    init {
        val blockPos = buf.readBlockPos()
        if (level.getBlockEntity(blockPos) is OvenBlockEntity) {
            blockEntity = level.getBlockEntity(blockPos) as OvenBlockEntity
            data = blockEntity!!.dataAccess
        }

        this.addSlot(Slot(blockEntity!!, INGREDIENT_SLOT, 36, 17))
        this.addSlot(Slot(blockEntity!!, EXTRA_INGREDIENT_SLOT, 124, 55))

        this.addSlot(OvenFuelSlot(this, blockEntity!!, FUEL_SLOT, 36, 53))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, RESULT_SLOT, 96, 35))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, EXTRA_RESULT_SLOT, 124, 16))

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
        var resultStack = ItemStack.EMPTY
        val slot = this.getSlot(index) ?: return resultStack
        if (!slot.hasItem()) return resultStack

        val slotStack = slot.item
        resultStack = slotStack.copy()

        if (index in 0..4) {
            // Move Stack to Player Inventory
            if (!moveItemStackTo(slotStack, 5, 41, true))
                return ItemStack.EMPTY
        } else if (!moveItemStackTo(slotStack, 0, 4, false))
            return ItemStack.EMPTY

        if (slotStack.isEmpty)
            slot.set(ItemStack.EMPTY)
        else
            slot.setChanged()

        return resultStack
    }

    fun canSmelt(stack: ItemStack): Boolean {
        return level.recipeManager.getRecipeFor(
            WitcheryRecipeTypes.OVEN_RECIPE_TYPE.get(), SingleRecipeInput(stack),
            this.level
        ).isPresent
    }

    fun isFuel(stack: ItemStack): Boolean {
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
        const val EXTRA_INGREDIENT_SLOT: Int = 3
        const val EXTRA_RESULT_SLOT: Int = 4
    }
}