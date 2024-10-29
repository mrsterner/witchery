package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetDataAttachment
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import java.util.*

class SyncPoppetDataS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(level: ServerLevel ,data: PoppetData) : this(CompoundTag().apply {

        val poppetListTag = ListTag().apply {
            data.poppetDataMap.forEach { dataItem ->
                val dataTag = CompoundTag()
                dataTag.put("blockPos", NbtUtils.writeBlockPos(dataItem.blockPos))
                dataTag.put("poppetItemStack", dataItem.poppetItemStack.save(level.registryAccess(), CompoundTag()))
                dataTag.putUUID("uuid", dataItem.uuid)
                add(dataTag)
            }
        }
        put("poppetData", poppetListTag)

        val cleanupMapTag = CompoundTag().apply {
            data.cleanupMap.forEach { (uuid, positions) ->
                val positionsTag = ListTag().apply {
                    positions.forEach { pos ->
                        add(NbtUtils.writeBlockPos(pos))
                    }
                }
                put(uuid.toString(), positionsTag)
            }
        }
        put("cleanupMap", cleanupMapTag)
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
        val poppetData = poppetListTag.mapNotNull { tag ->
            val compoundTag = tag as CompoundTag
            val blockPosOptional = NbtUtils.readBlockPos(compoundTag, "blockPos")
            val blockPos = blockPosOptional.orElse(null)

            if (blockPos != null) {
                val poppetItemStack = ItemStack.parse(context.registryAccess(), compoundTag.getCompound("poppetItemStack"))
                val uuid = compoundTag.getUUID("uuid")
                PoppetData.Data(uuid, blockPos, poppetItemStack.get())
            } else {
                null
            }
        }

        val cleanupMapTag = payload.nbt.getCompound("cleanupMap")
        val cleanupMap = mutableMapOf<UUID, MutableList<BlockPos>>()
        cleanupMapTag.allKeys.forEach { key ->
            val uuid = UUID.fromString(key)
            val positions = cleanupMapTag.getList(key, 10).mapNotNull { posTag ->
                val blockPosOptional = NbtUtils.readBlockPos(posTag as CompoundTag, "blockPos")
                blockPosOptional.orElse(null)
            }.toMutableList()
            cleanupMap[uuid] = positions
        }

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                PoppetDataAttachment.setPoppetData(player, PoppetData(poppetData.toMutableList(), cleanupMap))
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