package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.menu.SoulTradingMenu
import dev.sterner.witchery.content.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.content.entity.ImpEntity
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext

class SelectSoulTradeC2SPayload(val action: Action, val shift: Boolean, val index: Int) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(
        Action.values()[buf.readInt()],
        buf.readBoolean(),
        buf.readInt()
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeInt(action.ordinal)
        buf.writeBoolean(shift)
        buf.writeInt(index)
    }

    fun handleOnServer(ctx: IPayloadContext) {
        val player = ctx.player() as? ServerPlayer ?: return
        val menu = player.containerMenu as? SoulTradingMenu ?: return

        when (action) {
            Action.SELECT_TRADE -> {
                menu.selectTrade(index)
                syncMenuData(player, menu)
            }
            Action.SELECT_SOUL -> {
                menu.selectSoul(index)
                syncMenuData(player, menu)
            }
            Action.INCREMENT_AMOUNT -> {
                menu.incrementAmount(shift)
                syncMenuData(player, menu)
            }
            Action.DECREMENT_AMOUNT -> {
                menu.decrementAmount(shift)
                if (menu.tradeAmount == 0) {
                    menu.selectTrade(-1)
                }
                syncMenuData(player, menu)
            }
            Action.CONFIRM_TRADE -> {
                if (menu.canMakeTrade()) {
                    executeTrade(player, menu)
                } else {
                    syncMenuData(player, menu)
                }
            }
            Action.REQUEST_UPDATE -> {
                syncMenuData(player, menu)
            }
        }
    }

    private fun syncMenuData(player: ServerPlayer, menu: SoulTradingMenu) {
        val payload = SyncSoulTradeDataS2CPayload(
            menu.availableTrades,
            menu.availableSouls,
            menu.selectedTradeIndex,
            menu.selectedSoulIndex,
            menu.tradeAmount
        )
        PacketDistributor.sendToPlayer(player, payload)
    }

    private fun executeTrade(player: ServerPlayer, menu: SoulTradingMenu) {
        val trade = menu.getSelectedTrade() ?: return
        val soul = menu.getSelectedSoul() ?: return

        val level = player.serverLevel()
        val playerPos = player.blockPosition()
        val searchRadius = 16

        if (soul.isBlockEntity) {
            val minPos = BlockPos(
                playerPos.x - searchRadius.toInt(),
                playerPos.y - searchRadius.toInt(),
                playerPos.z - searchRadius.toInt()
            )
            val maxPos = BlockPos(
                playerPos.x + searchRadius.toInt(),
                playerPos.y + searchRadius.toInt(),
                playerPos.z + searchRadius.toInt()
            )

            for (pos in BlockPos.betweenClosed(minPos, maxPos)) {
                val blockEntity = level.getBlockEntity(pos) as? SoulCageBlockEntity
                if (blockEntity != null && blockEntity.hasSoul) {
                    blockEntity.extractSoul()
                    break
                }
            }
        } else {
            val entity = level.getEntity(soul.entityId) as? LivingEntity
            if (entity != null && EtherealEntityAttachment.getData(entity).isEthereal) {
                entity.discard()
            }
        }

        val outputStack = trade.output.copy()
        outputStack.count = menu.tradeAmount

        if (!player.inventory.add(outputStack.copy())) {
            player.drop(outputStack.copy(), false)
        }

        menu.selectTrade(-1)
        menu.selectSoul(-1)
        menu.tradeAmount = 1

        updateMenuData(player, menu)
    }

    private fun updateMenuData(player: ServerPlayer, menu: SoulTradingMenu) {
        val trades = ImpEntity.getAvailableTrades()
        val souls = findNearbySouls(player)

        menu.setTrades(trades)
        menu.setSouls(souls)

        syncMenuData(player, menu)
    }

    private fun findNearbySouls(player: ServerPlayer): List<SoulTradingMenu.SoulData> {
        val level = player.serverLevel()
        val souls = mutableListOf<SoulTradingMenu.SoulData>()
        val searchRadius = 16.0
        val playerPos = player.blockPosition()
        val searchBox = AABB(playerPos).inflate(searchRadius)

        val entities = level.getEntitiesOfClass(LivingEntity::class.java, searchBox) { entity ->
            entity != null && EtherealEntityAttachment.getData(entity).isEthereal
        }

        for (entity in entities) {
            val entityType = entity.type.toString()
            souls.add(SoulTradingMenu.SoulData(
                entityId = entity.id,
                weight = calculateSoulWeight(entity.type.toString()),
                entityType = entityType,
                isBlockEntity = false
            ))
        }

        val minPos = BlockPos(
            playerPos.x - searchRadius.toInt(),
            playerPos.y - searchRadius.toInt(),
            playerPos.z - searchRadius.toInt()
        )
        val maxPos = BlockPos(
            playerPos.x + searchRadius.toInt(),
            playerPos.y + searchRadius.toInt(),
            playerPos.z + searchRadius.toInt()
        )

        for (pos in BlockPos.betweenClosed(minPos, maxPos)) {
            val blockEntity = level.getBlockEntity(pos) as? SoulCageBlockEntity
            if (blockEntity != null && blockEntity.hasSoul) {
                souls.add(SoulTradingMenu.SoulData(
                    entityId = pos.asLong().toInt(),
                    weight = 20,
                    entityType = "minecraft:villager",
                    isBlockEntity = true
                ))
            }
        }

        return souls
    }

    private fun calculateSoulWeight(entityType: String): Int {
        return when {
            entityType.contains("villager", ignoreCase = true) -> 20
            entityType.contains("zombie", ignoreCase = true) -> 10
            entityType.contains("skeleton", ignoreCase = true) -> 10
            entityType.contains("creeper", ignoreCase = true) -> 12
            entityType.contains("spider", ignoreCase = true) -> 8
            entityType.contains("enderman", ignoreCase = true) -> 25
            entityType.contains("pig", ignoreCase = true) -> 6
            entityType.contains("cow", ignoreCase = true) -> 6
            else -> 5
        }
    }

    enum class Action {
        SELECT_TRADE,
        SELECT_SOUL,
        INCREMENT_AMOUNT,
        DECREMENT_AMOUNT,
        CONFIRM_TRADE,
        REQUEST_UPDATE
    }

    companion object {
        val ID: CustomPacketPayload.Type<SelectSoulTradeC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("select_soul_trade"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SelectSoulTradeC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SelectSoulTradeC2SPayload(buf) }
            )
    }
}