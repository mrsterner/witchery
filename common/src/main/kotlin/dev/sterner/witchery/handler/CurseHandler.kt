package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.BlockEvent
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.utils.value.IntValue
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.event.CurseEvent
import dev.sterner.witchery.platform.CursePlayerAttachment.Data
import dev.sterner.witchery.platform.CursePlayerAttachment.PlayerCurseData
import dev.sterner.witchery.platform.CursePlayerAttachment.getData
import dev.sterner.witchery.platform.CursePlayerAttachment.setData
import dev.sterner.witchery.registry.WitcheryCurseRegistry
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.EntityHitResult

object CurseHandler {

    /**
     * Tries to add a curse to a player. Triggers the on curse event if succeeded. Replaces the same curse.
     * @param player the target to be cursed
     * @param sourcePlayer the player who is cursing the other player
     */
    fun addCurse(player: Player, sourcePlayer: ServerPlayer?, curse: ResourceLocation, catBoosted: Boolean, duration: Int = 24000) {
        val data = getData(player).playerCurseList.toMutableList()
        val existingCurse = data.find { it.curseId == curse }
        val newCurseData = PlayerCurseData(curse, duration = duration, catBoosted)

        val result = CurseEvent.ON_CURSE.invoker().invoke(player, sourcePlayer, curse, catBoosted)
        if (result != EventResult.pass()) {
            return
        }

        if (existingCurse != null) {
            data.remove(existingCurse)
        }
        data.add(newCurseData)

        setData(player, Data(data))

        WitcheryCurseRegistry.CURSES[newCurseData.curseId]?.onAdded(
            player.level(),
            player,
            newCurseData.catBoosted
        )
    }


    /**
     * Removes a curse from the player while also trigger the onRemoved effect of th curse.
     */
    fun removeCurse(player: Player, curse: Curse) {
        val data = getData(player).playerCurseList.toMutableList()
        val curseIterator = data.iterator()

        while (curseIterator.hasNext()) {
            val curseData = curseIterator.next()

            if (curseData.curseId == WitcheryCurseRegistry.CURSES.getId(curse)) {
                WitcheryCurseRegistry.CURSES[curseData.curseId]?.onRemoved(
                    player.level(),
                    player,
                    curseData.catBoosted
                )
                curseIterator.remove()
                break
            }
        }

        setData(player, Data(data))
    }

    /**
     * Tick the curse effect and the duration of the curse. Runs the onRemoved effect when duration reaches 0.
     */
    private fun tickCurse(
        player: Player?
    ) {
        if (player == null) {
            return
        }

        val data = getData(player)
        if (data.playerCurseList.isEmpty()) {
            return
        }

        var dataModified = false
        val iterator = data.playerCurseList.iterator()

        while (iterator.hasNext()) {
            val curseData = iterator.next()
            if (curseData.duration > 0) {
                curseData.duration -= 1
                dataModified = true
                WitcheryCurseRegistry.CURSES[curseData.curseId]?.onTickCurse(
                    player.level(),
                    player,
                    curseData.catBoosted
                )
            }

            if (curseData.duration <= 0) {
                WitcheryCurseRegistry.CURSES[curseData.curseId]?.onRemoved(
                    player.level(),
                    player,
                    curseData.catBoosted
                )
                iterator.remove()
                dataModified = true
            }
        }

        if (dataModified) {
            setData(player, data)
        }

    }

    /**
     * Triggers the curses onHurt effect when the player is damaged.
     */
    private fun onHurt(
        livingEntity: LivingEntity?,
        damageSource: DamageSource?,
        amount: Float
    ): EventResult? {
        if (livingEntity is Player) {
            val data = getData(livingEntity)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)
                        ?.onHurt(livingEntity.level(), livingEntity, damageSource, amount, curse.catBoosted)
                }
            }
        }

        return EventResult.pass()
    }

    /**
     * Triggers the onBreak effect from cursed players who breaks blocks
     */
    private fun breakBlock(
        level: Level?,
        blockPos: BlockPos?,
        blockState: BlockState,
        serverPlayer: ServerPlayer?,
        intValue: IntValue?
    ): EventResult? {
        if (serverPlayer != null && level != null) {
            val data = getData(serverPlayer)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)
                        ?.breakBlock(level, serverPlayer, blockState, curse.catBoosted)
                }
            }
        }
        return EventResult.pass()
    }


    /**
     * Triggers the placeBlock effect of the curse when a player places a block.
     */
    private fun placeBlock(
        level: Level?,
        blockPos: BlockPos?,
        blockState: BlockState?,
        entity: Entity?
    ): EventResult? {
        if (entity is Player) {
            val data = getData(entity)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)
                        ?.placeBlock(level!!, entity, blockState, curse.catBoosted)
                }
            }
        }
        return EventResult.pass()
    }

    /**
     * Triggers the curses attackEntity when a player attacks another entity.
     */
    private fun attackEntity(
        player: Player?,
        level: Level?,
        target: Entity?,
        interactionHand: InteractionHand?,
        entityHitResult: EntityHitResult?
    ): EventResult? {
        if (player != null && target != null && entityHitResult != null) {
            val data = getData(player)
            if (data.playerCurseList.isNotEmpty()) {
                for (curse in data.playerCurseList) {
                    WitcheryCurseRegistry.CURSES.get(curse.curseId)
                        ?.attackEntity(level!!, player, target, entityHitResult, curse.catBoosted)
                }
            }
        }
        return EventResult.pass()
    }

    fun registerEvents() {
        EntityEvent.LIVING_HURT.register(::onHurt)
        BlockEvent.BREAK.register(::breakBlock)
        BlockEvent.PLACE.register(::placeBlock)
        PlayerEvent.ATTACK_ENTITY.register(::attackEntity)
        TickEvent.PLAYER_PRE.register(::tickCurse)
    }
}