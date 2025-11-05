package dev.sterner.witchery.content.block.altar

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.core.data.NaturePowerReloadListener
import dev.sterner.witchery.content.menu.AltarMenu
import dev.sterner.witchery.core.data.AltarAugmentReloadListener
import dev.sterner.witchery.network.AltarMultiplierSyncS2CPayload
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.util.RenderUtils
import dev.sterner.witchery.features.altar.ChunkedAltarPositionsAttachment
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
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.network.PacketDistributor
import kotlin.math.floor
import kotlin.math.min


class AltarBlockEntity(pos: BlockPos, state: BlockState) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.ALTAR.get(), AltarBlock.STRUCTURE.get(), pos, state
) {

    private var dirtyBlocks = mutableSetOf<BlockPos>()
    private var dirtyAugmentBlocks = mutableSetOf<BlockPos>()
    private var updateCooldown = 0

    var currentPower = 0
    var maxPower = 0
    var powerBoost = 1.0
    var powerMultiplier = 1.0
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
        markBlockDirty(blockPos)
    }

    fun markBlockDirty(pos: BlockPos) {
        dirtyBlocks.add(pos)
        updateCooldown = 5
    }

    fun markAugmentDirty(pos: BlockPos) {
        dirtyAugmentBlocks.add(pos)
        updateCooldown = 5
    }

    override fun onLoad() {
        super.onLoad()
        if (level is ServerLevel && !level!!.isClientSide) {
            ChunkedAltarPositionsAttachment.registerAltar(level as ServerLevel, blockPos)
        }
        markBlockDirty(blockPos)
    }

    override fun setRemoved() {
        super.setRemoved()
        if (level is ServerLevel && !level!!.isClientSide) {
            ChunkedAltarPositionsAttachment.unregisterAltar(level as ServerLevel, blockPos)
        }
    }


    private fun getLocalAABB() = AABB.ofSize(blockPos.center, range.toDouble(), range.toDouble(), range.toDouble())

    private fun collectAllLocalNaturePower(level: ServerLevel) {
        limitTracker.clear()
        maxPower = 0
        val aabb = getLocalAABB()
        level.getBlockStatesIfLoaded(aabb).forEach { state ->

            val power = NaturePowerReloadListener.getPower(state) ?: return@forEach
            val limit = NaturePowerReloadListener.getLimit(state) ?: return@forEach

            if (limitTracker.getOrDefault(limit.first, 0) >= limit.second) {
                return@forEach
            }
            maxPower += power
            limitTracker.compute(limit.first) { _, count -> count?.let { it + 1 } ?: 1 }
        }

        maxPower += (min(maxPower * powerBoost, 100000.0)).toInt()
    }

    private fun updateCurrentPower() {
        val rate = 10 * powerMultiplier
        currentPower = if (currentPower + rate >= maxPower) {
            maxPower
        } else {
            floor(currentPower + rate).toInt()
        }
    }

    fun getLocalAugmentAABB(direction: Direction): AABB {
        val pos = blockPos

        val (minX, minZ, maxX, maxZ) = when (direction) {
            Direction.NORTH -> Tuple4(pos.x - 1.0, pos.z.toDouble(), pos.x + 2.0, pos.z + 2.0)
            Direction.SOUTH -> Tuple4(pos.x - 1.0, pos.z - 1.0, pos.x + 2.0, pos.z + 1.0)
            Direction.WEST -> Tuple4(pos.x.toDouble(), pos.z - 1.0, pos.x + 2.0, pos.z + 2.0)
            Direction.EAST -> Tuple4(pos.x - 1.0, pos.z - 1.0, pos.x + 1.0, pos.z + 2.0)

            else -> Tuple4(pos.x.toDouble(), pos.z.toDouble(), pos.x + 2.0, pos.z + 2.0)
        }

        return AABB(
            minX,
            pos.y + 1.0,
            minZ,
            maxX,
            pos.y + 2.0,
            maxZ
        )
    }

    private data class Tuple4(val minX: Double, val minZ: Double, val maxX: Double, val maxZ: Double)

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (pPlayer is ServerPlayer) {
            openMenu(pPlayer)
        }
        return InteractionResult.SUCCESS
    }

    private fun openMenu(player: ServerPlayer) {
        PacketDistributor.sendToPlayer(player, AltarMultiplierSyncS2CPayload(blockPos, powerMultiplier))

        player.openMenu(object : MenuProvider {
            override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
                val buf = FriendlyByteBuf(Unpooled.buffer())
                buf.writeBlockPos(blockPos)
                buf.writeDouble(powerMultiplier)

                return AltarMenu(containerId, inventory, buf)
            }

            override fun getDisplayName(): Component {
                return Component.translatable("container.witchery.altar_menu")
            }
        }, blockPos)
    }

    fun augmentAltar(level: ServerLevel) {
        val augments: AABB = getLocalAugmentAABB(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))

        powerMultiplier = 1.0

        val prevPowerBoost = powerBoost
        powerBoost = 1.0

        var rangeMultiplier = 1.0
        val prevRange = range
        range = 16

        var bestLightAugment = 0.0
        var bestHeadAugment = 0.0
        var bestChaliceAugment = 0.0
        var hasPentacle = false
        var hasInfinityEgg = false

        level.getBlockStatesIfLoaded(augments).forEach { state ->
            val augment = AltarAugmentReloadListener.getAugment(state) ?: return@forEach
            val bonus = augment.bonus

            when (augment.category) {
                AltarAugmentReloadListener.AugmentCategory.LIGHT -> {
                    if (bonus.lightBonus > bestLightAugment) {
                        bestLightAugment = bonus.lightBonus
                    }
                }
                AltarAugmentReloadListener.AugmentCategory.HEAD -> {
                    if (bonus.headBonus > bestHeadAugment) {
                        bestHeadAugment = bonus.headBonus
                    }
                }
                AltarAugmentReloadListener.AugmentCategory.CHALICE -> {
                    if (bonus.chaliceBonus > bestChaliceAugment) {
                        bestChaliceAugment = bonus.chaliceBonus
                    }
                }
                AltarAugmentReloadListener.AugmentCategory.RANGE -> {
                    if (bonus.rangeMultiplier > rangeMultiplier) {
                        rangeMultiplier = bonus.rangeMultiplier
                    }
                }
                AltarAugmentReloadListener.AugmentCategory.SPECIAL -> {
                    if (bonus.hasPentacle) hasPentacle = true
                    if (bonus.hasInfinityEgg) hasInfinityEgg = true
                }
            }
        }

        powerMultiplier += powerMultiplier * bestLightAugment
        powerMultiplier += powerMultiplier * bestHeadAugment
        if (hasPentacle) {
            powerMultiplier *= 2
        }

        powerBoost += powerBoost * bestHeadAugment
        powerBoost += powerBoost * bestChaliceAugment

        range += (range * rangeMultiplier).toInt()

        if (hasInfinityEgg) {
            powerMultiplier *= 10
            powerBoost *= 2
        }

        if (powerBoost != prevPowerBoost || range != prevRange) {
            markBlockDirty(blockPos)
        }
    }

    override fun tickServer(serverLevel: ServerLevel) {
        if (updateCooldown > 0) {
            updateCooldown--

            if (updateCooldown == 0) {
                var needsPowerUpdate = false
                var needsAugmentUpdate = false

                if (dirtyBlocks.isNotEmpty()) {
                    needsPowerUpdate = true
                    dirtyBlocks.clear()
                }

                if (dirtyAugmentBlocks.isNotEmpty()) {
                    needsAugmentUpdate = true
                    dirtyAugmentBlocks.clear()
                }

                if (needsAugmentUpdate) {
                    augmentAltar(serverLevel)
                }

                if (needsPowerUpdate) {
                    collectAllLocalNaturePower(serverLevel)
                }
            }
        }

        if (ticks % 20 == 1) {
            updateCurrentPower()
        }

        if (ticks % 20 == 5) {
            augmentAltar(serverLevel)
        }

        if (ticks % 20 == 0 && Witchery.useDebugBoxRender()) {
            val augments = getLocalAugmentAABB(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
            RenderUtils.makeDebugAABB(augments, 0x00FF00, 40, serverLevel)

            val mainRange = getLocalAABB()
            RenderUtils.makeDebugAABB(mainRange, 0xFF0000, 40, serverLevel)
        }

        if (ticks % 20 > 5) {
            ticks = 0
        } else {
            ticks++
        }
    }


    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.putInt("currentPower", currentPower)
        tag.putInt("maxPower", maxPower)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        currentPower = tag.getInt("currentPower")
        maxPower = tag.getInt("maxPower")
    }

    fun consumeAltarPower(amount: Int, simulate: Boolean): Boolean {
        val hasPower = amount <= currentPower

        if (simulate || level?.isClientSide != false) {
            return hasPower
        } else if (!hasPower) {
            return false
        }

        currentPower -= amount
        return true
    }

    companion object {

        fun getClosestAltar(level: ServerLevel, pos: BlockPos, radius: Int): AltarBlockEntity? {
            val nearbyAltars = ChunkedAltarPositionsAttachment.findNearbyAltars(level, pos, radius)

            var closestAltar: AltarBlockEntity? = null
            var closestDistance = Double.MAX_VALUE

            for (altarPos in nearbyAltars) {
                val be = level.getBlockEntity(altarPos)
                if (be is AltarBlockEntity) {
                    val distance = pos.distSqr(altarPos)
                    if (distance < closestDistance) {
                        closestDistance = distance
                        closestAltar = be
                    }
                }
            }

            return closestAltar
        }

        fun onBlockBreak(event: BlockEvent.BreakEvent) {
            val level = event.level
            if (level !is ServerLevel) return

            val nearbyAltars = ChunkedAltarPositionsAttachment.findNearbyAltars(level, event.pos, 32)
            if (nearbyAltars.isEmpty()) return

            val eventPosCenter = event.pos.center

            for (altarPos in nearbyAltars) {
                val be = level.getBlockEntity(altarPos) as? AltarBlockEntity ?: continue

                if (be.getLocalAABB().contains(eventPosCenter)) {
                    be.markBlockDirty(event.pos)

                    val augmentAABB = be.getLocalAugmentAABB(
                        be.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                    )
                    if (augmentAABB.contains(eventPosCenter)) {
                        be.markAugmentDirty(event.pos)
                    }
                }
            }
        }

        fun onBlockPlace(event: BlockEvent.EntityPlaceEvent) {
            val level = event.level
            if (level !is ServerLevel) return

            val nearbyAltars = ChunkedAltarPositionsAttachment.findNearbyAltars(level, event.pos, 32)
            if (nearbyAltars.isEmpty()) return

            val eventPosCenter = event.pos.center

            for (altarPos in nearbyAltars) {
                val be = level.getBlockEntity(altarPos) as? AltarBlockEntity ?: continue

                if (be.getLocalAABB().contains(eventPosCenter)) {
                    be.markBlockDirty(event.pos)

                    val augmentAABB = be.getLocalAugmentAABB(
                        be.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                    )
                    if (augmentAABB.contains(eventPosCenter)) {
                        be.markAugmentDirty(event.pos)
                    }
                }
            }
        }
    }
}