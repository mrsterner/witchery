package dev.sterner.witchery.block.bear_trap


import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class BearTrapBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noCollission()) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.OPEN, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.OPEN)
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (level.getBlockEntity(pos) is BearTrapBlockEntity && entity is LivingEntity) {
            val be = level.getBlockEntity(pos) as BearTrapBlockEntity
            if (be.isOpen) {
                be.triggerBearTrap()
                entity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 30, 6))
            }

        }
        super.entityInside(state, level, pos, entity)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(2.0 / 16, 0.0, 2.0 / 16, 14.0 / 16.0, 4.0 / 16.0, 14.0 / 16.0)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BearTrapBlockEntity(pos, state)
    }
}