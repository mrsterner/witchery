package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncCovenS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import java.util.*

object CovenPlayerAttachment {
    @JvmStatic
    fun getData(player: Player): CovenPlayerAttachment.CovenData {
        return player.getData(WitcheryDataAttachments.COVEN_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: CovenPlayerAttachment.CovenData, sync: Boolean = true) {
        player.setData(WitcheryDataAttachments.COVEN_PLAYER_DATA_ATTACHMENT, data)
        if (sync) {
            CovenPlayerAttachment.sync(player, data)
        }
    }

    fun sync(player: Player, data: CovenData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncCovenS2CPayload(player, data)
            )
        }
    }

    data class CovenData(
        val covenWitches: List<WitchData> = listOf(),
        val playerMembers: List<UUID> = listOf(),
        val lastRitualTime: Long = 0L
    ) {
        companion object {
            val CODEC: Codec<CovenData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    WitchData.CODEC.listOf().fieldOf("covenWitches").forGetter { it.covenWitches },
                    UUIDUtil.CODEC.listOf().fieldOf("playerMembers").forGetter { it.playerMembers },
                    Codec.LONG.fieldOf("lastRitualTime").forGetter { it.lastRitualTime }
                ).apply(instance, ::CovenData)
            }

            val ID: ResourceLocation = Witchery.id("coven_data")
        }


        data class WitchData(
            val entityData: CompoundTag,
            val health: Float,
            val name: Component,
            val isActive: Boolean = true
        ) {
            companion object {
                private val COMPONENT_CODEC: Codec<Component> = Codec.STRING.xmap(
                    { str -> Component.literal(str) },
                    { component -> component.string }
                )

                val CODEC: Codec<WitchData> = RecordCodecBuilder.create { instance ->
                    instance.group(
                        CompoundTag.CODEC.fieldOf("entityData").forGetter { it.entityData },
                        Codec.FLOAT.fieldOf("health").forGetter { it.health },
                        COMPONENT_CODEC.fieldOf("name").forGetter { it.name },
                        Codec.BOOL.fieldOf("isActive").forGetter { it.isActive }
                    ).apply(instance, ::WitchData)
                }
            }
        }
    }
}