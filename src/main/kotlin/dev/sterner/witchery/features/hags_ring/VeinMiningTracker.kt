package dev.sterner.witchery.features.hags_ring

import dev.sterner.witchery.content.item.curios.HagsRingItem
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import java.util.*

object VeinMiningTracker {

    data class BreakingBlock(
        val pos: BlockPos,
        val state: BlockState,
        var breakTime: Int = 0,
        val maxBreakTime: Int,
        var breakProgress: Int = -1
    )

    private val playerVeins = mutableMapOf<UUID, MutableList<BreakingBlock>>()

    fun startVeinMining(player: ServerPlayer, orePositions: List<BlockPos>) {
        if (playerVeins.containsKey(player.uuid)) return

        val level = player.serverLevel()
        val breakingBlocks = orePositions.map { pos ->
            val state = level.getBlockState(pos)
            val destroySpeed = state.getDestroySpeed(level, pos)
            val maxBreakTime = (20 * destroySpeed).toInt().coerceAtLeast(1)
            BreakingBlock(pos, state, 0, maxBreakTime, -1)
        }

        playerVeins[player.uuid] = breakingBlocks.toMutableList()

        if (orePositions.isNotEmpty()) {
            val clickedPos = orePositions.last()

            val facePositions = generateFacePositions(clickedPos)
            for (facePos in facePositions) {
                level.sendParticles(
                    ParticleTypes.POOF,
                    facePos.x,
                    facePos.y,
                    facePos.z,
                    3,
                    0.1,
                    0.1,
                    0.1,
                    0.02
                )
            }

            level.playSound(
                null,
                clickedPos,
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.BLOCKS,
                0.8f,
                1.5f
            )
        }
    }

    /**
     * Generates particle spawn positions for all 6 faces of a block
     */
    private fun generateFacePositions(pos: BlockPos): List<Vec3> {
        val positions = mutableListOf<Vec3>()
        val center = 0.5
        val offset = 0.5

        positions.add(Vec3(pos.x + center, pos.y + center, pos.z - offset + 0.05))

        positions.add(Vec3(pos.x + center, pos.y + center, pos.z + offset + 0.95))

        positions.add(Vec3(pos.x - offset + 0.05, pos.y + center, pos.z + center))

        positions.add(Vec3(pos.x + offset + 0.95, pos.y + center, pos.z + center))

        positions.add(Vec3(pos.x + center, pos.y - offset + 0.05, pos.z + center))

        positions.add(Vec3(pos.x + center, pos.y + offset + 0.95, pos.z + center))

        return positions
    }

    fun tick(player: ServerPlayer) {
        val veins = playerVeins[player.uuid] ?: return
        val level = player.serverLevel()

        val fortuneLevel = HagsRingItem.getFortuneLevel(player)

        val iterator = veins.iterator()
        while (iterator.hasNext()) {
            val block = iterator.next()

            val currentState = level.getBlockState(block.pos)
            if (currentState.isAir || currentState != block.state) {
                level.destroyBlockProgress(player.id + generatePosHash(block.pos), block.pos, -1)
                iterator.remove()
                continue
            }

            block.breakTime++
            val progress = ((block.breakTime.toFloat() / block.maxBreakTime) * 10).toInt().coerceIn(0, 10)

            if (block.breakTime % 6 == 0) {
                level.playSound(
                    null,
                    block.pos,
                    block.state.soundType.breakSound,
                    SoundSource.BLOCKS,
                    0.5f,
                    1.0f
                )
            }

            if (progress != block.breakProgress) {
                block.breakProgress = progress
                level.destroyBlockProgress(player.id + generatePosHash(block.pos), block.pos, progress)
            }

            if (block.breakTime >= block.maxBreakTime) {
                val tool = if (fortuneLevel > 0) {
                    val tempTool = player.mainHandItem.copy()
                    tempTool.enchant(level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE), fortuneLevel)
                    tempTool
                } else {
                    player.mainHandItem
                }

                Block.dropResources(block.state, level, block.pos, level.getBlockEntity(block.pos), player, tool)

                level.removeBlock(block.pos, false)

                level.levelEvent(
                    LevelEvent.PARTICLES_DESTROY_BLOCK,
                    block.pos,
                    Block.getId(block.state)
                )

                level.destroyBlockProgress(player.id + generatePosHash(block.pos), block.pos, -1)
                iterator.remove()
            }
        }

        if (veins.isEmpty()) {
            playerVeins.remove(player.uuid)
        }
    }

    fun cancelVeinMining(player: ServerPlayer) {
        val veins = playerVeins.remove(player.uuid) ?: return
        val level = player.serverLevel()

        for (block in veins) {
            level.destroyBlockProgress(player.id + generatePosHash(block.pos), block.pos, -1)
        }
    }

    fun isVeinMining(player: ServerPlayer): Boolean {
        return playerVeins.containsKey(player.uuid)
    }

    private fun generatePosHash(blockPos: BlockPos): Int {
        return (31 * 31 * blockPos.x) + (31 * blockPos.y) + blockPos.z
    }
}