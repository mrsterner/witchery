package dev.sterner.witchery.content.block

import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HayBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.BlockHitResult


class BloodHayBlock(properties: Properties) : HayBlock(properties) {

    data class Coord(val x: Int, val y: Int)

    private val structure: List<Coord> = buildList {
        addAll(listOf(Coord(1, 0), Coord(2, 0)))
        addAll(listOf(Coord(1, 1), Coord(2, 1)))
        addAll(listOf(Coord(1, 2), Coord(2, 2)))
        addAll(listOf(Coord(0, 3), Coord(1, 3), Coord(2, 3), Coord(3, 3)))
        addAll(listOf(Coord(0, 4), Coord(1, 4), Coord(2, 4), Coord(3, 4)))
        addAll(listOf(Coord(1, 5), Coord(2, 5)))
        addAll(listOf(Coord(1, 6), Coord(2, 6)))
    }

    public override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {

        val stack: ItemStack = player.getItemInHand(hand)
        if (stack.`is`(Items.FLINT_AND_STEEL)) {
            val firePos = pos.relative(hitResult.direction)
            if (level.getBlockState(firePos).isAir) {
                level.playSound(
                    player, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f,
                    level.getRandom().nextFloat() * 0.4f + 0.8f
                )
                val fire = BaseFireBlock.getState(level, firePos)
                level.setBlock(firePos, fire, 11)
                level.gameEvent(player, GameEvent.BLOCK_PLACE, firePos)

                if (!player.isCreative) {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand))
                }

                if (checkBloodHayStructure(level, pos)) {
                    runRitual(level, pos)
                }

                return ItemInteractionResult.sidedSuccess(level.isClientSide())
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }


    private fun checkBloodHayStructure(level: Level, ignitePos: BlockPos): Boolean {
        val orientations = listOf<(Int, Int) -> Pair<Int, Int>>(
            { x, z -> Pair(x, z) },
            { x, z -> Pair(-x, z) },
            { x, z -> Pair(z, x) },
            { x, z -> Pair(z, -x) }
        )

        for (orient in orientations) {
            for (anchor in structure) {
                val (ax, az) = orient(anchor.x, 0)
                val baseX = ignitePos.x - ax
                val baseY = ignitePos.y - anchor.y
                val baseZ = ignitePos.z - az

                var allMatch = true
                for (coord in structure) {
                    val (dx, dz) = orient(coord.x, 0)
                    val checkPos = BlockPos(baseX + dx, baseY + coord.y, baseZ + dz)
                    if (level.getBlockState(checkPos).block !is BloodHayBlock) {
                        allMatch = false
                        break
                    }
                }
                if (allMatch) {
                    return true
                }
            }
        }
        return false
    }

    private fun runRitual(level: Level, basePos: BlockPos) {
        val random = level.random
        var spawnPos: BlockPos? = null

        for (i in 0 until 50) {
            val dx = random.nextInt(41) - 20
            val dz = random.nextInt(41) - 20
            val x = basePos.x + dx
            val z = basePos.z + dz
            val y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)
            val candidate = BlockPos(x, y, z)

            if (level.getBlockState(candidate.below()).isSolid &&
                level.getBlockState(candidate).isAir
            ) {
                spawnPos = candidate
                break
            }
        }

        if (spawnPos == null) {
            return
        }

        val orientations = listOf<(Int, Int) -> Pair<Int, Int>>(
            { x, z -> Pair(x, z) },
            { x, z -> Pair(-x, z) },
            { x, z -> Pair(z, x) },
            { x, z -> Pair(z, -x) }
        )
        for (orient in orientations) {
            for (coord in structure) {
                val (dx, dz) = orient(coord.x, 0)
                val hayPos = BlockPos(basePos.x + dx, basePos.y + coord.y, basePos.z + dz)

                if (level.getBlockState(hayPos).block is BloodHayBlock) {
                    for (dir in Direction.entries) {
                        val firePos = hayPos.relative(dir)
                        if (level.getBlockState(firePos).isAir) {
                            level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3)
                        }
                    }
                }
            }
        }

        val zombie = WitcheryEntityTypes.HORNED_HUNTSMAN.get().create(level)
        if (zombie != null) {
            zombie.moveTo(
                spawnPos.x + 0.5,
                spawnPos.y.toDouble(),
                spawnPos.z + 0.5,
                random.nextFloat() * 360f,
                0f
            )
            level.addFreshEntity(zombie)
        }
    }
}

