package dev.sterner.witchery.features.misc

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.SyncInventoryLockS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.network.PacketDistributor

object InventoryLockPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.INVENTORY_KEEPER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.INVENTORY_KEEPER_DATA_ATTACHMENT, data)
        sync(player, data)
    }


    fun sync(player: Player, data: Data) {
        if (player is ServerPlayer) {
            PacketDistributor.sendToPlayer(player, SyncInventoryLockS2CPayload(player, data))
        }
    }

    fun unlockAllSlots(player: Player) {
        setData(player, Data(emptySet()))
    }

    fun sync(player: ServerPlayer) {
        val data = getData(player)
        sync(player, data)
    }

    fun lockSlot(player: Player, slot: Int) {
        val data = getData(player)
        val newLockedSlots = data.lockedSlots.toMutableSet().apply { add(slot) }

        if (player is ServerPlayer) {
            dropItemFromSlot(player, slot)
        }

        setData(player, data.copy(lockedSlots = newLockedSlots))
    }

    fun unlockSlot(player: Player, slot: Int) {
        val data = getData(player)
        val newLockedSlots = data.lockedSlots.toMutableSet().apply { remove(slot) }
        setData(player, data.copy(lockedSlots = newLockedSlots))
    }

    fun unlockSlots(player: Player, slots: Set<Int>) {
        val data = getData(player)
        val newLockedSlots = data.lockedSlots.toMutableSet().apply { removeAll(slots) }
        setData(player, data.copy(lockedSlots = newLockedSlots))
    }


    fun lockSlots(player: Player, slots: Set<Int>) {
        val data = getData(player)
        val newLockedSlots = data.lockedSlots.toMutableSet().apply { addAll(slots) }

        if (player is ServerPlayer && !player.serverLevel().gameRules.getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            slots.forEach { slot ->
                if (!data.lockedSlots.contains(slot)) {
                    dropItemFromSlot(player, slot)
                }
            }
        }

        setData(player, data.copy(lockedSlots = newLockedSlots))
    }

    fun isSlotLocked(player: Player, slot: Int): Boolean {
        return getData(player).lockedSlots.contains(slot)
    }

    private fun dropItemFromSlot(player: ServerPlayer, slot: Int) {
        val item = player.inventory.getItem(slot)
        if (!item.isEmpty) {
            player.drop(item.copy(), false)
            player.inventory.setItem(slot, ItemStack.EMPTY)
        }
    }

    fun blockPlaceEvent(event: BlockEvent.EntityPlaceEvent) {
        val player = event.entity as? Player ?: return
        val data = getData(player)

        val selected = player.inventory.selected
        if (data.lockedSlots.contains(selected)) {
            event.isCanceled = true
        }
    }

    fun blockBreakEvent(event: BlockEvent.BreakEvent) {
        val player = event.player ?: return
        val data = getData(player)

        val selected = player.inventory.selected
        if (data.lockedSlots.contains(selected)) {
            event.isCanceled = true
        }
    }

    data class Data(
        val lockedSlots: Set<Int> = emptySet()
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.listOf().xmap(
                        { it.toSet() },
                        { it.toList() }
                    ).fieldOf("lockedSlots").forGetter { it.lockedSlots }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("inventory_lock_data")
        }
    }
}