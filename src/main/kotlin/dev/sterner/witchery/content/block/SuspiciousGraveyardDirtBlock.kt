package dev.sterner.witchery.content.block

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.gameevent.GameEvent

class SuspiciousGraveyardDirtBlock(
    val turnsInto: Block,
    val brushSound: SoundEvent,
    val brushCompletedSound: SoundEvent,
    properties: Properties
) : BaseEntityBlock(properties), Fallable {

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return CODEC
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get().create(pos, state)
    }

    init {
        this.registerDefaultState(stateDefinition.any().setValue(DUSTED, 0))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(DUSTED)
    }

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    public override fun onPlace(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        oldState: BlockState,
        movedByPiston: Boolean
    ) {
        level.scheduleTick(pos, this, 2)
    }

    public override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        level.scheduleTick(pos, this, 2)
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    public override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (level.getBlockEntity(pos) is SuspiciousGraveyardDirtBlockEntity) {
            var brushableBlockEntity = level.getBlockEntity(pos) as SuspiciousGraveyardDirtBlockEntity
            brushableBlockEntity.resetBrushingState()
        }

        if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.y >= level.minBuildHeight) {
            val fallingBlockEntity = FallingBlockEntity.fall(level, pos, state)
            fallingBlockEntity.disableDrop()
        }
    }

    override fun onBrokenAfterFall(level: Level, pos: BlockPos, fallingBlock: FallingBlockEntity) {
        val vec3 = fallingBlock.boundingBox.center
        level.levelEvent(2001, BlockPos.containing(vec3), getId(fallingBlock.blockState))
        level.gameEvent(fallingBlock, GameEvent.BLOCK_DESTROY, vec3)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (random.nextInt(16) == 0) {
            val blockPos = pos.below()
            if (FallingBlock.isFree(level.getBlockState(blockPos))) {
                val d = pos.x.toDouble() + random.nextDouble()
                val e = pos.y.toDouble() - 0.05
                val f = pos.z.toDouble() + random.nextDouble()
                level.addParticle(BlockParticleOption(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0)
            }
        }
    }


    companion object {
        val CODEC: MapCodec<SuspiciousGraveyardDirtBlock> =
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<SuspiciousGraveyardDirtBlock> ->
                instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("turns_into")
                        .forGetter { obj: SuspiciousGraveyardDirtBlock -> obj.turnsInto },
                    BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_sound")
                        .forGetter { obj: SuspiciousGraveyardDirtBlock -> obj.brushSound },
                    BuiltInRegistries.SOUND_EVENT.byNameCodec()
                        .fieldOf("brush_completed_sound")
                        .forGetter { obj: SuspiciousGraveyardDirtBlock -> obj.brushCompletedSound },
                    propertiesCodec()
                )
                    .apply(
                        instance
                    ) { turnsInto: Block, brushSound: SoundEvent, brushCompletedSound: SoundEvent, properties: Properties ->
                        SuspiciousGraveyardDirtBlock(
                            turnsInto,
                            brushSound,
                            brushCompletedSound,
                            properties
                        )
                    }
            }
        private val DUSTED: IntegerProperty = BlockStateProperties.DUSTED
        const val TICK_DELAY: Int = 2
    }
}