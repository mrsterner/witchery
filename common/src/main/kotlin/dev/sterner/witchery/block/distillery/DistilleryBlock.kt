package dev.sterner.witchery.block.distillery

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.block.cauldron.CauldronBlock.Companion.litBlockEmission
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos.z
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.awt.Color
import java.util.function.Supplier


class DistilleryBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noOcclusion().lightLevel(
    litBlockEmission(8)
)) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.LIT, false)
        )
    }


    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.DISTILLERY.get().create(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return shape
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.LIT)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (state.getValue(BlockStateProperties.LIT) as Boolean) {
            if (random.nextInt(5) == 0) {
                level.playLocalSound(
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 0.5,
                    pos.z.toDouble() + 0.5,
                    SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5f + random.nextFloat(),
                    random.nextFloat() * 0.7f + 0.6f,
                    false
                )
                val baseX = pos.x + 0.5
                val baseY = pos.y + 1.1
                val baseZ = pos.z + 0.5

                val offsets = listOf(
                    Pair(6.2f / 16f, 0f),    // +X face
                    Pair(-6.2f / 16f, 0f),   // -X face
                    Pair(0f, 6.2f / 16f),    // +Z face
                    Pair(0f, -6.2f / 16f)    // -Z face
                )

                for (offset in offsets) {
                    val offsetX = offset.first
                    val offsetZ = offset.second

                    val particleX = baseX + offsetX
                    val particleZ = baseZ + offsetZ


                    val minecraft = Minecraft.getInstance()

                    val manager: ParticleEngine = minecraft.particleEngine

                    val effectParticle = manager.createParticle(
                        ParticleTypes.EFFECT, particleX, baseY,
                        particleZ,  0.0, 0.0, 0.0
                    )

                    effectParticle?.setColor(1f,0.1f,1f)
                }
            }
        }
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        Containers.dropContentsOnDestroy(state, newState, level, pos)
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    companion object {
        val bottomShape = box(2.0, 0.0, 2.0, 14.0, 9.0, 14.0)
        val coreShape = box(4.0, 9.0, 4.0, 12.0, 16.0, 12.0)
        val shape = Shapes.join(bottomShape, coreShape, BooleanOp.OR)

        val STRUCTURE: Supplier<MultiBlockStructure> =
            Supplier<MultiBlockStructure> {
                (MultiBlockStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.DISTILLERY_COMPONENT.get().defaultBlockState()
                    )
                ))
            }
    }
}