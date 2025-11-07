package dev.sterner.witchery.content.menu

import dev.sterner.witchery.core.registry.WitcheryMenuTypes
import dev.sterner.witchery.content.entity.ImpEntity
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class SoulTradingMenu(id: Int, playerInventory: Inventory, buf: FriendlyByteBuf, val impEntity: ImpEntity) :
    AbstractContainerMenu(WitcheryMenuTypes.SOUL_TRADING_MENU_TYPE.get(), id) {

    constructor(id: Int, playerInventory: Inventory, buf: FriendlyByteBuf) : this(id, playerInventory, buf, playerInventory.player.level().getEntity(buf.readInt()) as ImpEntity)

    var availableTrades: List<SoulTrade> = listOf()
    var availableSouls: List<SoulData> = listOf()

    var selectedTrades: MutableList<SelectedTrade> = mutableListOf()
    var selectedSoulIndex: Int = -1

    init {
        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
    }

    fun setTrades(trades: List<SoulTrade>) {
        this.availableTrades = trades
    }

    fun setSouls(souls: List<SoulData>) {
        this.availableSouls = souls
    }

    fun selectSoul(index: Int) {
        if (index >= 0 && index < availableSouls.size) {
            selectedSoulIndex = index
        } else {
            selectedSoulIndex = -1
        }
    }

    fun incrementAmount(tradeIndex: Int, shift: Boolean = false) {
        val selected = selectedTrades.find { it.tradeIndex == tradeIndex }
        if (selected != null) {
            selected.amount = (selected.amount + if (shift) 5 else 1).coerceAtMost(64)
        } else if (selectedTrades.size < 3) {
            selectedTrades.add(SelectedTrade(tradeIndex, if (shift) 5 else 1))
        }
    }

    fun decrementAmount(tradeIndex: Int, shift: Boolean = false) {
        val selected = selectedTrades.find { it.tradeIndex == tradeIndex } ?: return
        selected.amount = (selected.amount - if (shift) 5 else 1)
        if (selected.amount <= 0) selectedTrades.remove(selected)
    }

    fun getTotalSoulCost(): Int {
        var total = 0
        for (selected in selectedTrades) {
            if (selected.tradeIndex >= 0 && selected.tradeIndex < availableTrades.size) {
                val trade = availableTrades[selected.tradeIndex]
                total += trade.soulCost * selected.amount
            }
        }
        return total
    }

    fun getSelectedSoul(): SoulData? {
        return if (selectedSoulIndex >= 0 && selectedSoulIndex < availableSouls.size) {
            availableSouls[selectedSoulIndex]
        } else null
    }

    fun canMakeTrade(): Boolean {
        if (selectedTrades.isEmpty()) return false
        val soul = getSelectedSoul() ?: return false
        return soul.weight >= getTotalSoulCost()
    }

    fun clearSelection() {
        selectedTrades.clear()
        selectedSoulIndex = -1
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        return impEntity.tradingPlayer == player
    }

    override fun removed(player: Player) {
        this.impEntity.tradingPlayer = null
        super.removed(player)
    }

    override fun canTakeItemForPickAll(stack: ItemStack, slot: Slot): Boolean {
        return false
    }

    data class SoulTrade(
        val output: ItemStack,
        val soulCost: Int
    )

    data class SoulData(
        val entityId: Int,
        val weight: Int,
        val entityType: String,
        val isBlockEntity: Boolean
    )

    data class SelectedTrade(
        val tradeIndex: Int,
        var amount: Int
    )
}