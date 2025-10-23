package dev.sterner.witchery.content.menu

import dev.sterner.witchery.content.block.spining_wheel.SpinningWheelBlockEntity
import dev.sterner.witchery.core.registry.WitcheryMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level


class SpinningWheelMenu(id: Int, val inventory: Inventory, buf: FriendlyByteBuf) :
    AbstractContainerMenu(WitcheryMenuTypes.SPINNING_WHEEL_MENU_TYPE.get(), id) {

    private var data: ContainerData = SimpleContainerData(2)
    private var level: Level = inventory.player.level()
    private var blockEntity: SpinningWheelBlockEntity? = null

    init {
        val blockPos = buf.readBlockPos()
        if (level.getBlockEntity(blockPos) is SpinningWheelBlockEntity) {
            blockEntity = level.getBlockEntity(blockPos) as SpinningWheelBlockEntity
            data = blockEntity!!.dataAccess
        }

        this.addSlot(Slot(blockEntity!!, SLOT_INPUT, 36 + 8, 15 + 18))
        this.addSlot(Slot(blockEntity!!, SLOT_EXTRA_INPUT_1, 36 + 36 + 8, 4 + 11))
        this.addSlot(Slot(blockEntity!!, SLOT_EXTRA_INPUT_2, 36 + 36 + 8, 4 + 18 + 11))
        this.addSlot(Slot(blockEntity!!, SLOT_EXTRA_INPUT_3, 36 + 36 + 8, 22 + 18 + 11))

        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT, 96 + 20, 33))

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

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var resultStack = ItemStack.EMPTY
        val slot = this.getSlot(index) ?: return resultStack
        if (!slot.hasItem()) return resultStack

        val slotStack = slot.item
        resultStack = slotStack.copy()

        if (index in 0..4) {
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

    override fun stillValid(player: Player): Boolean {
        return this.blockEntity!!.stillValid(player)
    }

    fun getBurnProgress(): Float {
        val i = data[0]
        val j = data[1]
        return if (j != 0 && i != 0) Mth.clamp(i.toFloat() / j.toFloat(), 0.0f, 1.0f) else 0.0f
    }

    companion object {
        const val SLOT_INPUT: Int = 0
        const val SLOT_EXTRA_INPUT_1: Int = 1
        const val SLOT_EXTRA_INPUT_2: Int = 2
        const val SLOT_EXTRA_INPUT_3: Int = 3

        const val SLOT_RESULT: Int = 4
    }
}