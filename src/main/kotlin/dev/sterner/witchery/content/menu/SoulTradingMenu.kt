package dev.sterner.witchery.content.menu

import dev.sterner.witchery.core.registry.WitcheryMenuTypes
import dev.sterner.witchery.content.entity.ImpEntity
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class SoulTradingMenu(id: Int, playerInventory: Inventory, buf: FriendlyByteBuf) :
    AbstractContainerMenu(WitcheryMenuTypes.SOUL_TRADING_MENU_TYPE.get(), id) {

    private var trader: ImpEntity? = null
    var availableTrades: List<SoulTrade> = listOf()
    var availableSouls: List<SoulData> = listOf()
    var selectedTradeIndex: Int = -1
    var selectedSoulIndex: Int = -1
    var tradeAmount: Int = 1

    init {
        trader = Minecraft.getInstance().level?.getEntity(buf.readVarInt()) as ImpEntity?

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

    fun selectTrade(index: Int) {
        if (index >= 0 && index < availableTrades.size) {
            selectedTradeIndex = index
            tradeAmount = 1
        } else {
            selectedTradeIndex = -1
        }
    }

    fun selectSoul(index: Int) {
        if (index >= 0 && index < availableSouls.size) {
            selectedSoulIndex = index
        } else {
            selectedSoulIndex = -1
        }
    }

    fun incrementAmount(shift: Boolean = false) {
        if (selectedTradeIndex >= 0) {
            tradeAmount = (tradeAmount + if(shift) 5 else 1).coerceAtMost(64)
        }
    }

    fun decrementAmount(shift: Boolean = false) {
        if (selectedTradeIndex >= 0) {
            tradeAmount = (tradeAmount - if(shift) 5 else 1).coerceAtLeast(0)
            if (tradeAmount <= 0) {
                selectedTradeIndex = -1
                tradeAmount = 0
            }
        }
    }

    fun getSelectedTrade(): SoulTrade? {
        return if (selectedTradeIndex >= 0 && selectedTradeIndex < availableTrades.size) {
            availableTrades[selectedTradeIndex]
        } else null
    }

    fun getSelectedSoul(): SoulData? {
        return if (selectedSoulIndex >= 0 && selectedSoulIndex < availableSouls.size) {
            availableSouls[selectedSoulIndex]
        } else null
    }

    fun canMakeTrade(): Boolean {
        val trade = getSelectedTrade() ?: return false
        val soul = getSelectedSoul() ?: return false
        return soul.weight >= (trade.soulCost * tradeAmount)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        return true //TODO this.trader?.tradingPlayer == player
    }

    override fun removed(player: Player) {
        this.trader?.tradingPlayer = null
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
}