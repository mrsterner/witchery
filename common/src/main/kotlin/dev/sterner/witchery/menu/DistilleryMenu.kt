package dev.sterner.witchery.menu

import dev.sterner.witchery.block.distillery.DistilleryBlockEntity
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class DistilleryMenu(id: Int, inventory: Inventory, buf: FriendlyByteBuf) :
    AbstractContainerMenu(WitcheryMenuTypes.DISTILLERY_MENU_TYPE.get(), id) {

    private var data: ContainerData = SimpleContainerData(4)
    private var level: Level = inventory.player.level()
    private var blockEntity: DistilleryBlockEntity? = null

    init {
        val blockPos = buf.readBlockPos()
        if (level.getBlockEntity(blockPos) is DistilleryBlockEntity) {
            blockEntity = level.getBlockEntity(blockPos) as DistilleryBlockEntity
            data = blockEntity!!.dataAccess
        }

        this.addSlot(Slot(blockEntity!!, SLOT_INPUT, 36, 17))
        this.addSlot(Slot(blockEntity!!, SLOT_EXTRA_INPUT, 124, 55))
        this.addSlot(object : Slot(blockEntity!!, SLOT_JAR, 36, 53) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return stack.`is`(WitcheryItems.JAR.get())
            }
        })

        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_1, 96, 35))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_2, 124, 16))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_3, 96 + 18, 35))
        this.addSlot(FurnaceResultSlot(inventory.player, blockEntity!!, SLOT_RESULT_4, 124 + 18, 16))

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
        return ItemStack.EMPTY // TODO
    }

    override fun stillValid(player: Player): Boolean {
        return this.blockEntity!!.stillValid(player)
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