package dev.sterner.witchery.data_attachment.possession


import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.api.interfaces.Possessable
import dev.sterner.witchery.payload.SyncPossessedDataS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor

object PossessedDataAttachment {

    fun get(entity: Entity): Data {
        return entity.getData(WitcheryDataAttachments.POSSESSED_DATA)
    }

    fun set(entity: Entity, data: Data) {
        entity.setData(WitcheryDataAttachments.POSSESSED_DATA, data)
        syncToClient(entity)
    }

    fun syncToClient(entity: Entity) {
        if (!entity.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntity(
                entity,
                SyncPossessedDataS2CPayload(entity.id, get(entity), entity.level().registryAccess())
            )
        }
    }

    data class Data(
        var hungerDatai: CompoundTag? = null,
        var inventory: SimpleContainer? = null,
        var selectedSlot: Int = 0
    ) {

        fun getHungerData(): CompoundTag {
            if (hungerDatai == null) {
                hungerDatai = CompoundTag().apply {
                    putInt("foodLevel", 20)
                }
            }
            return hungerDatai!!
        }

        fun moveItems(playerInventory: Inventory, fromPlayerToThis: Boolean) {
            if (playerInventory.player.level().isClientSide) return

            if (fromPlayerToThis) {
                dropItems(playerInventory.player)
                inventory = SimpleContainer(playerInventory.containerSize)

                for (i in 0 until playerInventory.containerSize) {
                    val stack = playerInventory.getItem(i)
                    inventory!!.setItem(i, playerInventory.removeItem(i, stack.count))
                }

                selectedSlot = playerInventory.selected
                onPossessed(playerInventory.player)
            } else {
                inventory?.let { inv ->
                    for (i in 0 until inv.containerSize) {
                        playerInventory.player.drop(playerInventory.removeItem(i, 64), false)
                        playerInventory.setItem(i, inv.removeItem(i, 64))
                    }
                    inventory = null
                    playerInventory.selected = selectedSlot

                    if (playerInventory.player is ServerPlayer) {

                        (playerInventory.player as ServerPlayer).connection.send(
                            ClientboundSetCarriedItemPacket(
                                selectedSlot
                            )
                        )
                    }
                }
            }
        }

        fun dropItems(entity: Entity) {
            inventory?.let { inv ->
                for (i in 0 until inv.containerSize) {
                    val stack = inv.removeItem(i, 64)
                    if (!stack.isEmpty) {
                        entity.spawnAtLocation(stack)
                    }
                }
            }
        }

        private fun onPossessed(entity: Entity) {

        }

        fun copyFrom(original: Data, registryAccess: HolderLookup.Provider) {
            this.hungerDatai = original.hungerDatai?.copy()
            this.selectedSlot = original.selectedSlot

            original.inventory?.let { origInv ->
                this.inventory = SimpleContainer(origInv.containerSize)
                for (i in 0 until origInv.containerSize) {
                    this.inventory!!.setItem(i, origInv.getItem(i).copy())
                }
            }
        }

        companion object {
            fun codec(registryAccess: HolderLookup.Provider): Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    CompoundTag.CODEC.optionalFieldOf("hungerData", CompoundTag()).forGetter { it.hungerDatai ?: CompoundTag() },
                    Codec.INT.fieldOf("selectedSlot").forGetter { it.selectedSlot },
                    CompoundTag.CODEC.optionalFieldOf("inventory", CompoundTag()).xmap(
                        { tag -> deserializeInventory(tag, registryAccess) },
                        { inv -> serializeInventory(inv, registryAccess) }
                    ).forGetter { it.inventory ?: SimpleContainer(0) }
                ).apply(instance) { hunger, slot, inv ->
                    Data().apply {
                        hungerDatai = if (hunger.isEmpty) null else hunger
                        selectedSlot = slot
                        inventory = if (inv!!.containerSize > 0) inv else null
                    }
                }
            }

            val CODEC: Codec<Data> = Codec.unit(Data())

            private fun serializeInventory(inventory: Container?, provider: HolderLookup.Provider): CompoundTag {
                val tag = CompoundTag()
                if (inventory != null) {
                    tag.putInt("size", inventory.containerSize)
                    val items = ListTag()
                    for (i in 0 until inventory.containerSize) {
                        val stack = inventory.getItem(i)
                        if (!stack.isEmpty) {
                            val itemTag = CompoundTag()
                            itemTag.putInt("Slot", i)
                            stack.save(provider, itemTag)
                            items.add(itemTag)
                        }
                    }
                    tag.put("Items", items)
                }
                return tag
            }

            private fun deserializeInventory(tag: CompoundTag, provider: HolderLookup.Provider): SimpleContainer? {
                if (!tag.contains("size")) return null

                val size = tag.getInt("size")
                val inventory = SimpleContainer(size)
                val items = tag.getList("Items", 10)

                for (i in 0 until items.size) {
                    val itemTag = items.getCompound(i)
                    val slot = itemTag.getInt("Slot")
                    if (slot >= 0 && slot < size) {
                        inventory.setItem(slot, ItemStack.parse(provider, itemTag).get())
                    }
                }

                return inventory
            }
        }
    }

    fun onMobConverted(original: LivingEntity, converted: LivingEntity) {
        val possessor = (original as? Possessable)?.possessor
        val possessedData = get(converted)

        if (possessor != null) {
            PossessionComponentAttachment.get(possessor).stopPossessing(false)
            (converted as? Possessable)?.setPossessor(possessor)
        }

        possessedData.copyFrom(get(original), original.level().registryAccess())
        set(converted, possessedData)
    }
}