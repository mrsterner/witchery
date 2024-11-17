package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import java.awt.Color

class SpawnTransfixParticlesS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(vec3: Vec3, spawnRed: Boolean) : this(CompoundTag().apply {
        putDouble("x", vec3.x)
        putDouble("y", vec3.y)
        putDouble("z", vec3.z)
        putBoolean("r", spawnRed)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SpawnTransfixParticlesS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val x = payload.nbt.getDouble("x")
        val y = payload.nbt.getDouble("y")
        val z = payload.nbt.getDouble("z")
        val shouldSpawnRed = payload.nbt.getBoolean("r")

        val randX = x + Mth.nextDouble(client.level!!.random, -0.25, 0.25)
        val randY = (y + 1.8)
        val randZ = z + Mth.nextDouble(client.level!!.random, -0.25, 0.25)

        val color = if (shouldSpawnRed) Color(250,50, 0).rgb else Color(30,255, 255).rgb
        val r = ((color shr 16) and 0xFF) / 255.0f
        val g = ((color shr 8) and 0xFF) / 255.0f
        val b = (color and 0xFF) / 255.0f

        client.execute {
            val manager: ParticleEngine = client.particleEngine

            val effectParticle = manager.createParticle(
                ParticleTypes.EFFECT, randX, randY, randZ,
                0.0, 0.0, 0.0
            )
            effectParticle?.setColor(r, g, b)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SpawnTransfixParticlesS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("spawn_transfix"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SpawnTransfixParticlesS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SpawnTransfixParticlesS2CPayload(buf!!) }
            )
    }
}