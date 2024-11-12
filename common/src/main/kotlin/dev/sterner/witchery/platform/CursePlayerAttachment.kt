package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.EventResult
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.utils.value.IntValue
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
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
import java.awt.Component

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

    fun addCurse(player: Player, curse: ResourceLocation) {
        val data = getData(player).playerCurseList.toMutableList()
        val existingCurse = data.find { it.curseId == curse }
        val newCurseData = PlayerCurseData(curse, duration = 24000)

        if (existingCurse != null) {
            data.remove(existingCurse)
        }

        data.add(newCurseData)

        setData(player, Data(data))
    }

    fun removeCurse(player: Player, curse: Curse) {
        val data = getData(player)
        val curseIterator = data.playerCurseList.iterator()

        while (curseIterator.hasNext()) {
            val curseData = curseIterator.next()

            if (curseData.curseId == curse.id) {
                curseIterator.remove()
                break
            }
        }

        setData(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncCurseS2CPacket(player, data))
        }
    }

    fun tickCurse(player: Player?) {
        if (player != null) {
            val data = getData(player)
            var dataModified = false
            val iterator = data.playerCurseList.iterator()

            while (iterator.hasNext()) {
                val curseData = iterator.next()
                println("${curseData.curseId} : ${curseData.duration}")
                if (curseData.duration > 0) {
                    curseData.duration -= 1
                    dataModified = true
                    WitcheryCurseRegistry.CURSES[curseData.curseId]?.onTickCurse(player.level(), player)
                }

                if (curseData.duration <= 0) {
                    iterator.remove()
                    dataModified = true
                }
            }

            if (dataModified) {
                setData(player, data)
            }
        }
    }

    fun onHurt(livingEntity: LivingEntity?, damageSource: DamageSource?, fl: Float): EventResult? {
        if (livingEntity is Player) {
            val data = getData(livingEntity)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)?.onHurt(livingEntity.level(), livingEntity, damageSource, fl)
                }
            }
        }

        return EventResult.pass()
    }

    fun breakBlock(level: Level?, blockPos: BlockPos?, blockState: BlockState, serverPlayer: ServerPlayer?, intValue: IntValue?): EventResult? {
        if (serverPlayer != null) {
            val data = getData(serverPlayer)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)?.breakBlock(level!!, serverPlayer, blockState)
                }
            }
        }

        return EventResult.pass()
    }

    fun placeBlock(level: Level?, blockPos: BlockPos?, blockState: BlockState?, entity: Entity?): EventResult? {
        if (entity is Player) {
            val data = getData(entity)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)?.placeBlock(level!!, entity, blockState)
                }
            }
        }
        return EventResult.pass()
    }

    fun attackEntity(player: Player?, level: Level?, target: Entity?, interactionHand: InteractionHand?, entityHitResult: EntityHitResult?): EventResult? {
        if (player != null && target != null && entityHitResult != null) {
            val data = getData(player)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)?.attackEntity(level!!, player, target, entityHitResult)
                }
            }
        }
        return EventResult.pass()
    }

    data class PlayerCurseData(val curseId: ResourceLocation, var duration: Int) {

        companion object {
            val CODEC: Codec<PlayerCurseData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("curseId").forGetter { it.curseId },
                    Codec.INT.fieldOf("duration").forGetter { it.duration }

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