package dev.sterner.witchery.menu

import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

class AltarMenu(containerId: Int, inventory: Inventory, buf: FriendlyByteBuf) :
    AbstractContainerMenu(WitcheryMenuTypes.ALTAR_MENU_TYPE.get(), containerId) {

    var data: ContainerData = SimpleContainerData(2)
    var altar: AltarBlockEntity? = null

    init {
        altar = inventory.player.level().getBlockEntity(buf.readBlockPos(), WitcheryBlockEntityTypes.ALTAR.get())
            .getOrNull()
        altar?.let { altar ->
            data = altar.data
        }

        this.addDataSlots(data)
    }

    fun getCurrentPower() = data[0]

    fun getMaxPower() = data[1]

    override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY

    override fun stillValid(player: Player) = true
}