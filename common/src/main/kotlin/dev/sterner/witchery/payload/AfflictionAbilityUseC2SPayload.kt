package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload


class AfflictionAbilityUseC2SPayload(val abilityIndex: Int) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readInt())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeInt(abilityIndex)
    }

    fun handleC2S(payload: AfflictionAbilityUseC2SPayload, context: NetworkManager.PacketContext?) {
        val player = context?.player ?: return

        val abilities = AfflictionAbilityHandler.getAbilities(player)
        val ability = abilities.getOrNull(payload.abilityIndex) ?: return

        if (!AbilityCooldownManager.isOnCooldown(player, ability)) {
            ability.use(player)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<AfflictionAbilityUseC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("affliction_use_ability"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, AfflictionAbilityUseC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> AfflictionAbilityUseC2SPayload(buf) }
            )
    }
}