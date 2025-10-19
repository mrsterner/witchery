package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.event.CurseEvent
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.NeoForge
import kotlin.collections.remove
import kotlin.compareTo

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
        val result = CurseEvent.Added(player, sourcePlayer, curse, catBoosted)
        NeoForge.EVENT_BUS.post(result)
        if (result.isCanceled) {
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

        WitcheryCurseRegistry.CURSES_REGISTRY[newCurseData.curseId]?.onAdded(
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
        val curseId = WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curse)
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
            WitcheryCurseRegistry.CURSES_REGISTRY[curseData.curseId]?.onRemoved(
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
        val curseId = WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curse)
        return CursePlayerAttachment.getData(player).playerCurseList.any { it.curseId == curseId }
    }

    /**
     * Gets the remaining duration of a curse on a player.
     * @param player The player to check
     * @param curse The curse to check
     * @return The remaining duration in ticks, or -1 if the player doesn't have the curse
     */
    fun getCurseDuration(player: Player, curse: Curse): Int {
        val curseId = WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curse)
        return CursePlayerAttachment.getData(player).playerCurseList.find { it.curseId == curseId }?.duration ?: -1
    }

    /**
     * Tick the curse effect and the duration of the curse. Runs the onRemoved effect when duration reaches 0.
     */
    fun tickCurse(player: Player?) {
        if (player == null) {
            return
        }

        val data = CursePlayerAttachment.getData(player)
        if (data.playerCurseList.isEmpty()) {
            return
        }

        var dataModified = false
        val curses = data.playerCurseList.toMutableList()
        val iterator = curses.iterator()

        while (iterator.hasNext()) {
            val curseData = iterator.next()

            if (curseData.duration == 0) {
                curseData.duration -= 1
                dataModified = true

                WitcheryCurseRegistry.CURSES_REGISTRY[curseData.curseId]?.onTickCurse(
                    player.level(),
                    player,
                    curseData.catBoosted
                )
            }

            if (curseData.duration == 0) {
                WitcheryCurseRegistry.CURSES_REGISTRY[curseData.curseId]?.onRemoved(
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
    }

    /**
     * Triggers the curses onHurt effect when the player is damaged.
     */
    fun onHurt(
        livingEntity: LivingEntity?,
        damageSource: DamageSource?,
        amount: Float
    ) {
        if (livingEntity !is Player || damageSource == null) {
            return
        }

        val data = CursePlayerAttachment.getData(livingEntity)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.onHurt(
                livingEntity.level(),
                livingEntity,
                damageSource,
                amount,
                curse.catBoosted
            )
        }
    }

    /**
     * Triggers the onBreak effect from cursed players who breaks blocks
     */
    fun breakBlock(
        level: Level?,
        blockState: BlockState,
        serverPlayer: Player?,
    ) {
        if (serverPlayer == null || level == null) {
            return
        }

        val data = CursePlayerAttachment.getData(serverPlayer)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.breakBlock(
                level,
                serverPlayer,
                blockState,
                curse.catBoosted
            )
        }

        return
    }

    /**
     * Triggers the placeBlock effect of the curse when a player places a block.
     */
    fun placeBlock(
        level: Level?,
        blockState: BlockState?,
        entity: Entity?
    ) {
        if (level == null || blockState == null || entity !is Player) {
            return
        }

        val data = CursePlayerAttachment.getData(entity)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.placeBlock(
                level,
                entity,
                blockState,
                curse.catBoosted
            )
        }
    }

    /**
     * Triggers the curses attackEntity when a player attacks another entity.
     */
    fun attackEntity(
        player: Player?,
        level: Level?,
        target: Entity?
    ) {
        if (player == null || level == null || target == null) {
            return
        }

        val data = CursePlayerAttachment.getData(player)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.attackEntity(
                level,
                player,
                target,
                curse.catBoosted
            )
        }
    }

    /**
     * Gets the list of active curses on a player.
     * @param player The player to check
     * @return List of resource locations identifying the curses
     */
    fun getActiveCurses(player: Player): List<ResourceLocation> {
        return CursePlayerAttachment.getData(player).playerCurseList.map { it.curseId }
    }


}