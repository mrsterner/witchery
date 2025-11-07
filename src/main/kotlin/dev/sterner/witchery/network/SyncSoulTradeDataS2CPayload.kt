package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.menu.SoulTradingMenu
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.ItemStack

class SyncSoulTradeDataS2CPayload(
    val trades: List<SoulTradingMenu.SoulTrade>,
    val souls: List<SoulTradingMenu.SoulData>,
    val selectedTradeIndex: Int,
    val selectedSoulIndex: Int,
    val tradeAmount: Int
) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(
        readTrades(buf),
        readSouls(buf),
        buf.readInt(),
        buf.readInt(),
        buf.readInt()
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeInt(trades.size)
        for (trade in trades) {
            ItemStack.STREAM_CODEC.encode(buf, trade.output)
            buf.writeInt(trade.soulCost)
        }

        buf.writeInt(souls.size)
        for (soul in souls) {
            buf.writeInt(soul.entityId)
            buf.writeInt(soul.weight)
            buf.writeUtf(soul.entityType)
            buf.writeBoolean(soul.isBlockEntity)
        }

        buf.writeInt(selectedTradeIndex)
        buf.writeInt(selectedSoulIndex)
        buf.writeInt(tradeAmount)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        client.execute {
            val menu = client.player?.containerMenu as? SoulTradingMenu ?: return@execute
            menu.setTrades(trades)
            menu.setSouls(souls)
            menu.selectedTradeIndex = selectedTradeIndex
            menu.selectedSoulIndex = selectedSoulIndex
            menu.tradeAmount = tradeAmount
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncSoulTradeDataS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_soul_trade_data"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncSoulTradeDataS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncSoulTradeDataS2CPayload(buf) }
            )

        private fun readTrades(buf: RegistryFriendlyByteBuf): List<SoulTradingMenu.SoulTrade> {
            val size = buf.readInt()
            val trades = mutableListOf<SoulTradingMenu.SoulTrade>()
            for (i in 0 until size) {
                val output = ItemStack.STREAM_CODEC.decode(buf)
                val soulCost = buf.readInt()
                trades.add(SoulTradingMenu.SoulTrade(output, soulCost))
            }
            return trades
        }

        private fun readSouls(buf: RegistryFriendlyByteBuf): List<SoulTradingMenu.SoulData> {
            val size = buf.readInt()
            val souls = mutableListOf<SoulTradingMenu.SoulData>()
            for (i in 0 until size) {
                val entityId = buf.readInt()
                val weight = buf.readInt()
                val entityType = buf.readUtf()
                val isBlockEntity = buf.readBoolean()
                souls.add(SoulTradingMenu.SoulData(entityId, weight, entityType, isBlockEntity))
            }
            return souls
        }
    }
}