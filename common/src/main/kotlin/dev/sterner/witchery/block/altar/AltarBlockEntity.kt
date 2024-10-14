package dev.sterner.witchery.block.altar

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.BlockEvent
import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.data.NaturePowerHandler
import dev.sterner.witchery.menu.AltarMenu
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import kotlin.math.floor


class AltarBlockEntity(pos: BlockPos, state: BlockState) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.ALTAR.get(), AltarBlock.STRUCTURE.get(), pos, state) {

    init {
        // May remove these tbh if it gets too laggy
        BlockEvent.PLACE.register { level, pos, state, entity ->
            if (level is ServerLevel && getLocalAABB().contains(pos.center) && NaturePowerHandler.getPower(state.block) != null)
                collectAllLocalNaturePower(level)

            EventResult.pass()
        }
        BlockEvent.BREAK.register { level, pos, state, player, xp ->
            if (level is ServerLevel && getLocalAABB().contains(pos.center) && NaturePowerHandler.getPower(state.block) != null)
                collectAllLocalNaturePower(level)

            EventResult.pass()
        }
    }

    var currentPower = 0
    var maxPower = 0
    var powerMultiplier = 1.0 // Turned double to allow for more options (candles), will have to manually sync with client
    var range = 16

    val data = object: ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> currentPower
                1 -> maxPower
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {}

        override fun getCount() = 2
    }

    val limitTracker = mutableMapOf<ResourceLocation, Int>()
    var ticks = 0

    fun getLocalAABB() = AABB.ofSize(blockPos.center, range.toDouble(), range.toDouble(), range.toDouble())

    // Based on speed and potential lag, lets call this every 5 or so seconds
    fun collectAllLocalNaturePower(level: ServerLevel) {
        maxPower = 0
        val aabb = getLocalAABB()
        level.getBlockStatesIfLoaded(aabb).forEach { state ->
            val power = NaturePowerHandler.getPower(state.block) ?: return@forEach
            val limit = NaturePowerHandler.getLimit(state.block) ?: return@forEach
            if (limitTracker.getOrDefault(limit.first, 0) > limit.second)
                return@forEach
            maxPower += power
            limitTracker.compute(limit.first) { _, count -> count?.let { it + 1 } ?: 1 }
        }
    }

    fun updateCurrentPower() {
        val rate = 10 * powerMultiplier
        if (currentPower + rate >= maxPower)
            currentPower = maxPower
        else
            currentPower = floor(currentPower + rate).toInt()
    }

    fun augmentAltar(level: ServerLevel, corePos: BlockPos) {
        // Do augmentation stuff here
        // Remember, certain augments effects dont stack, they take the best of em.
        // updating range
        // Updating multiplier
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (pPlayer is ServerPlayer)
            openMenu(pPlayer)
        return InteractionResult.SUCCESS
    }

    fun openMenu(player: ServerPlayer) {
        MenuRegistry.openExtendedMenu(player, object : ExtendedMenuProvider {
            override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
                val buf = FriendlyByteBuf(Unpooled.buffer())
                saveExtraData(buf)
                return AltarMenu(i, inventory, buf)
            }

            override fun getDisplayName() = Component.translatable("container.witchery.altar_menu")

            override fun saveExtraData(buf: FriendlyByteBuf) {
                buf.writeBlockPos(blockPos)
            }
        })
    }

    override fun init(level: Level, pos: BlockPos, state: BlockState) {
        if (level !is ServerLevel) return

        if (ticks % 20 == 5)
            collectAllLocalNaturePower(level)
        else if (ticks % 20 == 1) {
            augmentAltar(level, pos)
            updateCurrentPower()
        }

        if (ticks % 20 >= 5)
            ticks = 0
        else
            ticks++
    }
}