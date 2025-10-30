package dev.sterner.witchery.content.block.bear_trap


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.core.registry.WitcheryMobEffects
import net.minecraft.core.BlockPos
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Predicate

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
                val bl = entity is Player
                entity.addEffect(MobEffectInstance(WitcheryMobEffects.BEAR_TRAP_INCAPACITATED, if (bl) 20 * 15 else 20 * 30, 0, true, false))
            }
        }
        super.entityInside(state, level, pos, entity)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        val aabb = state.getShape(level, pos).bounds()
            .move(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            .inflate(0.5)

        val entities: MutableList<LivingEntity> = mutableListOf()
        level.getEntities(
            EntityTypeTest.forClass(LivingEntity::class.java),
            aabb,
            Predicate { it.hasEffect(WitcheryMobEffects.BEAR_TRAP_INCAPACITATED) },
            entities
        )
        entities.forEach { it.removeEffect(WitcheryMobEffects.BEAR_TRAP_INCAPACITATED) }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(2.0 / 16, 0.0, 2.0 / 16, 14.0 / 16.0, 4.0 / 16.0, 14.0 / 16.0)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BearTrapBlockEntity(pos, state)
    }
}