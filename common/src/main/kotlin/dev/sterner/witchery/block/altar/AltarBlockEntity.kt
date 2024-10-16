package dev.sterner.witchery.block.altar

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.BlockEvent
import dev.architectury.networking.NetworkManager
import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.data.NaturePowerHandler
import dev.sterner.witchery.menu.AltarMenu
import dev.sterner.witchery.payload.AltarMultiplierSyncS2CPacket
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import java.util.Optional
import kotlin.math.floor


class AltarBlockEntity(pos: BlockPos, state: BlockState) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.ALTAR.get(), AltarBlock.STRUCTURE.get(), pos, state
) {

    var powerUpdateQueued = false
    var augmentUpdateQueued = false

    var currentPower = 0
    var maxPower = 0
    var powerMultiplier =
        1.0 // Turned double to allow for more options (candles), will have to manually sync with client
    var range = 16

    val data = object : ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> currentPower
                1 -> maxPower
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> currentPower = value
                1 -> maxPower = value
            }
        }

        override fun getCount() = 2
    }

    val limitTracker = mutableMapOf<ResourceLocation, Int>()
    var ticks = 0

    init {
        powerUpdateQueued = true
        augmentUpdateQueued = true
    }

    private fun getLocalAABB() = AABB.ofSize(blockPos.center, range.toDouble(), range.toDouble(), range.toDouble())

    // Based on speed and potential lag, lets call this every 5 or so seconds
    private fun collectAllLocalNaturePower(level: ServerLevel) {
        limitTracker.clear()
        maxPower = 0
        val aabb = getLocalAABB()
        level.getBlockStatesIfLoaded(aabb).forEach { state ->

            val power = NaturePowerHandler.getPower(state) ?: return@forEach
            val limit = NaturePowerHandler.getLimit(state) ?: return@forEach
            if (limitTracker.getOrDefault(limit.first, 0) >= limit.second)
                return@forEach
            maxPower += power
            limitTracker.compute(limit.first) { _, count -> count?.let { it + 1 } ?: 1 }
        }
    }

    private fun updateCurrentPower() {
        val rate = 10 * powerMultiplier
        if (currentPower + rate >= maxPower)
            currentPower = maxPower
        else
            currentPower = floor(currentPower + rate).toInt()
    }

    fun getLocalAugmentAABB(direction: Direction): AABB {
        val forwardVec = direction.opposite.normal
        val sidewaysVec = direction.counterClockWise.normal
        val aabb = AABB(blockPos).move(0.0, 1.0, 0.0)
            .expandTowards(forwardVec.x.toDouble(), 0.0, forwardVec.z.toDouble())
            .expandTowards(sidewaysVec.x.toDouble(), 0.0, sidewaysVec.z.toDouble())
            .expandTowards(-sidewaysVec.x.toDouble(), 0.0, -sidewaysVec.z.toDouble())
        return aabb.setMaxX(aabb.maxX - 0.4).setMaxY(aabb.maxY - 0.4).setMaxZ(aabb.maxZ - 0.4)
    }

    fun augmentAltar(level: ServerLevel) {
        val augments = getLocalAugmentAABB(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
        // Do Stuff
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (pPlayer is ServerPlayer) {
            openMenu(pPlayer)
            return InteractionResult.SUCCESS
        }
        return super.onUseWithoutItem(pPlayer)
    }

    private fun openMenu(player: ServerPlayer) {
        NetworkManager.sendToPlayer(player, AltarMultiplierSyncS2CPacket(blockPos, powerMultiplier))

        MenuRegistry.openExtendedMenu(player, object : ExtendedMenuProvider {
            override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
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

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level !is ServerLevel) return

        if (powerUpdateQueued) {
            collectAllLocalNaturePower(level)
            powerUpdateQueued = false
        }

        if (augmentUpdateQueued) {
            augmentAltar(level)
            augmentUpdateQueued = false
        }

        if (ticks % 20 == 0) {
            updateCurrentPower()
        }


        if (ticks / 20.0 >= 1)
            ticks = 0
        else
            ticks++
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.putInt("currentPower", currentPower)
        tag.putInt("maxPower", maxPower)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)

        currentPower = pTag.getInt("currentPower")
        maxPower = pTag.getInt("maxPower")
    }

    fun consumeAltarPower(amount: Int, simulate: Boolean): Boolean {
        val hasPower = amount <= currentPower

        if (simulate || level?.isClientSide != false)
            return hasPower
        else if (!hasPower)
            return false

        currentPower -= amount
        return true
    }
}