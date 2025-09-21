package dev.sterner.witchery.block.werewolf_altar


import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.block.bear_trap.BearTrapBlock
import dev.sterner.witchery.entity.WerewolfEntity
import dev.sterner.witchery.payload.SpawnItemParticlesS2CPayload
import dev.sterner.witchery.registry.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor

class WerewolfAltarBlockEntity(
    blockPos: BlockPos, blockState: BlockState
) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.WEREWOLF_ALTAR.get(),
    WerewolfAltarBlock.STRUCTURE.get(),
    blockPos,
    blockState
) {

    var items: NonNullList<ItemStack> = NonNullList.withSize(1, ItemStack.EMPTY)

    private var conversionTicks = 0
    private var muttonCounter = 0

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        if (items[0].isEmpty && pStack.`is`(WitcheryTags.WEREWOLF_ALTAR_ITEM)) {
            items[0] = pStack
        } else {
            if (pPlayer.mainHandItem.isEmpty) {
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, items[0])
                items.clear()
            }
        }
        setChanged()

        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun tickServer(level: ServerLevel) {
        super.tickServer(level)
        if (level is ServerLevel) {
            val item = items[0]
            if (!item.isEmpty) {

                if (item.`is`(Items.GOLD_INGOT)) {
                    conversionTicks++

                    spawnConsumeParticles(level, item)

                    if (conversionTicks >= 20 * 2) {
                        conversionTicks = 0
                        items.clear()
                        items[0] = WitcheryItems.MOON_CHARM.get().defaultInstance
                        setChanged()
                    }
                } else if (item.`is`(Items.MUTTON)) {
                    conversionTicks++

                    spawnConsumeParticles(level, item)

                    if (conversionTicks >= 20 * 2) {
                        conversionTicks = 0
                        muttonCounter++
                        items.clear()
                        setChanged()
                    }
                }
            }

            lookForWerewolf(level, blockPos)
        }
    }

    private fun lookForWerewolf(level: ServerLevel, pos: BlockPos) {
        if (level.gameTime % 20 == 0L) {
            val box = AABB(pos).inflate(4.0)
            val traps = BlockPos.betweenClosedStream(box).filter { level.getBlockState(it).block is BearTrapBlock }
            traps.forEach { blockPos ->
                val trapAabb = AABB(blockPos).inflate(0.0, 1.0, 0.0)
                val werewolves = level.getEntities(WitcheryEntityTypes.WEREWOLF.get(), trapAabb) { it.isAlive }
                werewolves.forEach { entity ->
                    entity.entityData.set(WerewolfEntity.CAN_INFECT, true)
                }
            }
        }

    }

    private fun spawnConsumeParticles(level: ServerLevel, itemStack: ItemStack) {
        if (level.random.nextFloat() < 0.3) {
            val dir = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            val offset = Vec3(-0.35, 0.4, 0.9)

            val rotatedOffset = when (dir) {
                Direction.NORTH -> offset
                Direction.SOUTH -> Vec3(-offset.x, offset.y, -offset.z)
                Direction.WEST -> Vec3(offset.z, offset.y, -offset.x)
                Direction.EAST -> Vec3(-offset.z, offset.y, offset.x)
                else -> offset
            }

            val spawnPos = blockPos.center.add(rotatedOffset)
            PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos(blockPos), SpawnItemParticlesS2CPayload(spawnPos, itemStack))
        }
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        this.items = NonNullList.withSize(1, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries)
        conversionTicks = pTag.getInt("ConversionTicks")
        muttonCounter = pTag.getInt("MuttonCounter")
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, this.items, registries)
        tag.putInt("ConversionTicks", conversionTicks)
        tag.putInt("MuttonCounter", muttonCounter)
    }
}