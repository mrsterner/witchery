package dev.sterner.witchery.network

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.handling.IPayloadContext


class GrantWitcheryAdvancementsC2SPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor() : this(CompoundTag().apply {
        putBoolean("grantAdvancements", true)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnServer(ctx: IPayloadContext) {
        val player: Player? = ctx.player()
        val shouldGrant = nbt.getBoolean("grantAdvancements")

        if (player is ServerPlayer && shouldGrant) {
            grantAllWitcheryAdvancements(player)
        }
    }

    private fun grantAllWitcheryAdvancements(player: ServerPlayer) {
        val server = player.server ?: return
        val advancementManager = server.advancements
        var grantedCount = 0

        for (holder in advancementManager.allAdvancements) {
            val id = holder.id()

            if (id.namespace == "witchery") {
                val advancement = holder.value()
                val progress = player.advancements.getOrStartProgress(holder)

                if (!progress.isDone) {
                    var wasCompleted = false
                    for (criterion in advancement.criteria.keys) {
                        if (!progress.getCriterion(criterion)!!.isDone) {
                            player.advancements.award(holder, criterion)
                            wasCompleted = true
                        }
                    }
                    if (wasCompleted) {
                        grantedCount++
                    }
                }
            }
        }

        player.sendSystemMessage(Component.literal("Granted $grantedCount Witchery advancements!"))
    }

    companion object {
        val ID: CustomPacketPayload.Type<GrantWitcheryAdvancementsC2SPayload> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath("witchery", "grant_witchery_advancements"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, GrantWitcheryAdvancementsC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> GrantWitcheryAdvancementsC2SPayload(buf) }
            )
    }
}