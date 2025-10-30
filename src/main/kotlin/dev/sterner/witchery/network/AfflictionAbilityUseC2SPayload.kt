package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.affliction.ability.AbilityCooldownManager
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext


class AfflictionAbilityUseC2SPayload(val abilityIndex: Int) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readInt())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeInt(abilityIndex)
    }

    fun handleOnServer(ctx: IPayloadContext) {
        val player = ctx.player() ?: return

        val abilities = AfflictionAbilityHandler.getAbilities(player)

        val ability = abilities.getOrNull(abilityIndex)

        if (ability == null) {
            return
        }

        val isOnCooldown = AbilityCooldownManager.isOnCooldown(player, ability)

        if (!isOnCooldown) {
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