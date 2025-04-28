package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.BlockEvent
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.utils.value.IntValue
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.event.CurseEvent
import dev.sterner.witchery.payload.SyncCurseS2CPacket
import dev.sterner.witchery.registry.WitcheryCurseRegistry
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.EntityHitResult

object CursePlayerAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(player: Player, data: Data) {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncCurseS2CPacket(player, data))
        }
    }

    data class PlayerCurseData(val curseId: ResourceLocation, var duration: Int, var catBoosted: Boolean) {

        companion object {
            val CODEC: Codec<PlayerCurseData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("curseId").forGetter { it.curseId },
                    Codec.INT.fieldOf("duration").forGetter { it.duration },
                    Codec.BOOL.fieldOf("catBoosted").forGetter { it.catBoosted }

                ).apply(instance, ::PlayerCurseData)
            }
        }
    }

    data class Data(var playerCurseList: MutableList<PlayerCurseData> = mutableListOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("player_curse_list")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(PlayerCurseData.CODEC).fieldOf("playerCurseList").forGetter { it.playerCurseList }
                ).apply(instance, ::Data)
            }
        }
    }
}