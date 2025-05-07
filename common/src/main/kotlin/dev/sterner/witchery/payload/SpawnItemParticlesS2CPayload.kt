package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

class SpawnItemParticlesS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(vec3: Vec3, stack: ItemStack) : this(CompoundTag().apply {
        putDouble("x", vec3.x)
        putDouble("y", vec3.y)
        putDouble("z", vec3.z)
        val result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack)
        result.resultOrPartial { _ -> }?.let {
            put("stack", it.get())
        }
    })


    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SpawnItemParticlesS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val x = payload.nbt.getDouble("x")
        val y = payload.nbt.getDouble("y")
        val z = payload.nbt.getDouble("z")

        val result = ItemStack.CODEC.parse(NbtOps.INSTANCE, payload.nbt.getCompound("stack"))
        val itemStack = result.result().orElse(ItemStack.EMPTY)

        client.execute {
            client.level?.addParticle(
                ItemParticleOption(ParticleTypes.ITEM, itemStack),
                x + 0.0,
                y + 0.0,
                z + 0.0,
                0.0,
                0.05,
                0.0
            )
        }
    }


    companion object {
        val ID: CustomPacketPayload.Type<SpawnItemParticlesS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("spawn_item_particle"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SpawnItemParticlesS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SpawnItemParticlesS2CPayload(buf) }
            )
    }
}