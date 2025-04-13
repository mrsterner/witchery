package dev.sterner.witchery.menu

import dev.sterner.witchery.block.distillery.DistilleryBlockEntity
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class DistilleryMenu(id: Int, inventory: Inventory, buf: FriendlyByteBuf) :
    AbstractContainerMenu(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), id) {

    private var data: ContainerData = SimpleContainerData(2)
    private var level: Level = inventory.player.level()
    private var blockEntity: DistilleryBlockEntity? = null

    init {
        val blockPos = buf.readBlockPos()
        if (level.getBlockEntity(blockPos) is DistilleryBlockEntity) {
            blockEntity = level.getBlockEntity(blockPos) as DistilleryBlockEntity
            data = blockEntity!!.dataAccess
        }

        this.addSlot(Slot(blockEntity!!, SLOT_INPUT, 36 - 9, 17))
        this.addSlot(Slot(blockEntity!!, SLOT_EXTRA_INPUT, 36 + 9, 17))
        this.addSlot(object : Slot(blockEntity!!, SLOT_JAR, 36, 53) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return stack.`is`(WitcheryItems.JAR.get())
            }
        })

        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_1, 96 + 18, 35 + 9))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_2, 124 - 9 - 1, 16 + 9 + 1))

        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_3, 96 + 18 + 18, 35 + 9))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_4, 124 + 18 - 9 - 1, 16 + 9 + 1))

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

        if (index in 0..6) {
            if (!moveItemStackTo(slotStack, 7, 43, true))
                return ItemStack.EMPTY
        } else {
            when {
                isJar(slotStack) -> {
                    if (!moveItemStackTo(slotStack, 2, 3, false))
                        return ItemStack.EMPTY
                }
                else -> {
                    if (!moveItemStackTo(slotStack, 0, 6, false))
                        return ItemStack.EMPTY
                }
            }
        }

        if (slotStack.isEmpty) {
            slot.set(ItemStack.EMPTY)
        } else {
            slot.setChanged()
        }

        return resultStack
    }


    fun isJar(stack: ItemStack): Boolean {
        return stack.`is`(WitcheryItems.JAR.get())
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
        const val SLOT_EXTRA_INPUT: Int = 1
        const val SLOT_JAR: Int = 2

        const val SLOT_RESULT_1: Int = 3
        const val SLOT_RESULT_2: Int = 4
        const val SLOT_RESULT_3: Int = 5
        const val SLOT_RESULT_4: Int = 6
    }
}