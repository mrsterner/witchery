package dev.sterner.witchery.payload

import dev.sterner.witchery.data_attachment.possession.OrderedInventory
import dev.sterner.witchery.data_attachment.possession.PossessionAttachment
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity

class SyncPossessedDataS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(entity: LivingEntity, data: PossessionAttachment.PossessedEntityData) : this(CompoundTag().apply {
        putUUID("EntityId", entity.uuid)

        // Manually encode the possessed data including inventory
        val dataTag = CompoundTag()
        data.hungerData?.let { dataTag.put("hunger_data", it) }
        dataTag.putInt("selected_slot", data.selectedSlot)
        dataTag.putBoolean("converted_under_possession", data.convertedUnderPossession)

        // Handle inventory separately
        data.inventory?.let { inv ->
            dataTag.putInt("inventory_size", inv.containerSize)
            dataTag.put("inventory", inv.createTag(entity.registryAccess()))
        }

        put("PossessedData", dataTag)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val entityId = nbt.getUUID("EntityId")

        level.entities.get(entityId)?.let { entity ->
            if (entity is LivingEntity) {
                val dataTag = nbt.getCompound("PossessedData")

                client.execute {
                    val data = PossessionAttachment.PossessedEntityData()

                    if (dataTag.contains("hunger_data")) {
                        data.hungerData = dataTag.getCompound("hunger_data")
                    }

                    data.selectedSlot = dataTag.getInt("selected_slot")
                    data.convertedUnderPossession = dataTag.getBoolean("converted_under_possession")

                    if (dataTag.contains("inventory_size")) {
                        val size = dataTag.getInt("inventory_size")
                        val inv = OrderedInventory(size)
                        inv.fromTag(dataTag.getList("inventory", 10), entity.registryAccess())
                        data.inventory = inv
                    }

                    entity.setData(WitcheryDataAttachments.POSSESSED_DATA, data)
                }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPossessedDataS2CPayload> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath("yourmod", "sync_possessed_data"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPossessedDataS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPossessedDataS2CPayload(buf) }
            )
    }
}