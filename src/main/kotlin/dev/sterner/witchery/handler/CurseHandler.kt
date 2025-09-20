package dev.sterner.witchery.handler

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.event.CurseEvent
import dev.sterner.witchery.data_attachment.CursePlayerAttachment
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
     * @param player The target to be cursed
     * @param sourcePlayer The player who is cursing the other player (can be null)
     * @param curse The identifier of the curse to apply
     * @param catBoosted Whether the curse is boosted by cat magic
     * @param duration Duration of the curse in ticks (default 24000, which is 20 minutes in real time)
     * @return Boolean indicating if the curse was successfully applied
     */
    fun addCurse(
        player: Player,
        sourcePlayer: ServerPlayer?,
        curse: ResourceLocation,
        catBoosted: Boolean,
        duration: Int = 24000
    ): Boolean {
        // Early check: only proceed if the curse event allows it
        val result = CurseEvent.ON_CURSE.invoker().invoke(player, sourcePlayer, curse, catBoosted)
        if (result != EventResult.pass()) {
            return false
        }

        val data = CursePlayerAttachment.getData(player).playerCurseList.toMutableList()
        val existingCurse = data.find { it.curseId == curse }
        val newCurseData = CursePlayerAttachment.PlayerCurseData(curse, duration = duration, catBoosted = catBoosted)

        if (existingCurse != null) {
            data.remove(existingCurse)
        }
        data.add(newCurseData)

        CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(data))

        WitcheryCurseRegistry.CURSES.registry.get()[newCurseData.curseId]?.onAdded(
            player.level(),
            player,
            newCurseData.catBoosted
        )

        return true
    }

    /**
     * Removes a curse from the player while also triggering the onRemoved effect of the curse.
     * @param player The player to remove the curse from
     * @param curse The curse to remove
     * @return Boolean indicating if the curse was found and removed
     */
    fun removeCurse(player: Player, curse: Curse): Boolean {
        val data = CursePlayerAttachment.getData(player).playerCurseList.toMutableList()
        val curseId = WitcheryCurseRegistry.CURSES.registry.get().getKey(curse)
        val curseData = data.find { it.curseId == curseId } ?: return false

        curse.onRemoved(player.level(), player, curseData.catBoosted)

        data.remove(curseData)
        CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(data))

        return true
    }

    /**
     * Removes all curses from a player.
     * @param player The player to remove curses from
     * @return The number of curses removed
     */
    fun removeAllCurses(player: Player): Int {
        val data = CursePlayerAttachment.getData(player).playerCurseList
        if (data.isEmpty()) return 0

        val count = data.size
        val level = player.level()

        data.forEach { curseData ->
            WitcheryCurseRegistry.CURSES.registry.get()[curseData.curseId]?.onRemoved(
                level,
                player,
                curseData.catBoosted
            )
        }

        CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(mutableListOf()))

        return count
    }

    /**
     * Checks if a player has a specific curse.
     * @param player The player to check
     * @param curse The curse to check for
     * @return Boolean indicating if the player has the curse
     */
    fun hasCurse(player: Player, curse: Curse): Boolean {
        val curseId = WitcheryCurseRegistry.CURSES.registry.get().getKey(curse)
        return CursePlayerAttachment.getData(player).playerCurseList.any { it.curseId == curseId }
    }

    /**
     * Gets the remaining duration of a curse on a player.
     * @param player The player to check
     * @param curse The curse to check
     * @return The remaining duration in ticks, or -1 if the player doesn't have the curse
     */
    fun getCurseDuration(player: Player, curse: Curse): Int {
        val curseId = WitcheryCurseRegistry.CURSES.registry.get().getKey(curse)
        return CursePlayerAttachment.getData(player).playerCurseList.find { it.curseId == curseId }?.duration ?: -1
    }

    /**
     * Tick the curse effect and the duration of the curse. Runs the onRemoved effect when duration reaches 0.
     */
    private fun tickCurse(player: Player?): EventResult {
        if (player == null) {
            return EventResult.pass()
        }

        val data = getData(player)
        if (data.playerCurseList.isEmpty()) {
            return EventResult.pass()
        }

        var dataModified = false
        val curses = data.playerCurseList.toMutableList()
        val iterator = curses.iterator()

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
            CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(curses))
        }

        return EventResult.pass()
    }

    /**
     * Triggers the curses onHurt effect when the player is damaged.
     */
    private fun onHurt(
        livingEntity: LivingEntity?,
        damageSource: DamageSource?,
        amount: Float
    ): EventResult {
        if (livingEntity !is Player || damageSource == null) {
            return EventResult.pass()
        }

        val data = CursePlayerAttachment.getData(livingEntity)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES.registry.get()[curse.curseId]?.onHurt(
                livingEntity.level(),
                livingEntity,
                damageSource,
                amount,
                curse.catBoosted
            )
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
    ): EventResult {
        if (serverPlayer == null || level == null) {
            return EventResult.pass()
        }

        val data = getData(serverPlayer)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES[curse.curseId]?.breakBlock(
                level,
                serverPlayer,
                blockState,
                curse.catBoosted
            )
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
    ): EventResult {
        if (level == null || blockState == null || entity !is Player) {
            return EventResult.pass()
        }

        val data = CursePlayerAttachment.getData(entity)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES.registry.get()[curse.curseId]?.placeBlock(
                level,
                entity,
                blockState,
                curse.catBoosted
            )
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
    ): EventResult {
        if (player == null || level == null || target == null || entityHitResult == null) {
            return EventResult.pass()
        }

        val data = getData(player)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES[curse.curseId]?.attackEntity(
                level,
                player,
                target,
                entityHitResult,
                curse.catBoosted
            )
        }

        return EventResult.pass()
    }

    /**
     * Gets the list of active curses on a player.
     * @param player The player to check
     * @return List of resource locations identifying the curses
     */
    fun getActiveCurses(player: Player): List<ResourceLocation> {
        return CursePlayerAttachment.getData(player).playerCurseList.map { it.curseId }
    }

    /**
     * Register all event handlers for curse functionality.
     */
    fun registerEvents() {
        EntityEvent.LIVING_HURT.register(::onHurt)
        BlockEvent.BREAK.register(::breakBlock)
        BlockEvent.PLACE.register(::placeBlock)
        PlayerEvent.ATTACK_ENTITY.register(::attackEntity)
        TickEvent.PLAYER_PRE.register(::tickCurse)
    }
}