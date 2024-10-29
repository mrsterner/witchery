package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetDataAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class SyncPoppetDataS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: PoppetData): this(CompoundTag().apply {
        putUUID("Id", player.uuid)

        val poppetListTag = ListTag().apply {
            data.poppetData.forEach { dataItem ->
                val dataTag = CompoundTag()
                dataTag.put("blockPos", NbtUtils.writeBlockPos(dataItem.blockPos))
                dataTag.put("poppetItemStack", dataItem.poppetItemStack.save(player.registryAccess(), CompoundTag()))
                add(dataTag)
            }
        }
        put("poppetData", poppetListTag)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncPoppetDataS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()
        val id = payload.nbt.getUUID("Id")

        val poppetListTag = payload.nbt.getList("poppetData", 10)
        val poppetData = poppetListTag.map { tag ->
            val compoundTag = tag as CompoundTag
            val blockPos = NbtUtils.readBlockPos(compoundTag, "blockPos")
            val poppetItemStack = ItemStack.parse(context.registryAccess(), compoundTag.getCompound("poppetItemStack"))
            PoppetData.Data(blockPos.get(), poppetItemStack.get())
        }

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                PoppetDataAttachment.setPoppetData(player, PoppetData(poppetData.toMutableList()))
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncPoppetDataS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_poppet"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncPoppetDataS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPoppetDataS2CPacket(buf!!) }
            )
    }
}