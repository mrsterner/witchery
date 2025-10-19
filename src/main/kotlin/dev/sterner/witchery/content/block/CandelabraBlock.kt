package dev.sterner.witchery.content.block

import com.mojang.serialization.MapCodec
import dev.sterner.witchery.content.block.cauldron.CauldronBlock.Companion.litBlockEmission
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractCandleBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape


class CandelabraBlock(properties: Properties) :
    AbstractCandleBlock(properties.noOcclusion().lightLevel(litBlockEmission(13))) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun codec(): MapCodec<CandelabraBlock> = simpleCodec(::CandelabraBlock)

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.LIT)
    }

    override fun getParticleOffsets(state: BlockState) = mutableListOf(
        Vec3(0.5, 1.1, 0.5),
        Vec3(0.2, 1.0, 0.5),
        Vec3(0.8, 1.0, 0.5),
        Vec3(0.5, 1.0, 0.8),
        Vec3(0.5, 1.0, 0.2)
    )

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (stack.isEmpty && player.abilities.mayBuild && state.getValue(BlockStateProperties.LIT)) {
            extinguish(player, state, level, pos)
            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        } else if (stack.`is`(Items.FIRE_CHARGE) && canBeLit(state)) {
            // Taken from FireChargeItem class
            level.playSound(
                null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f,
                (level.random.nextFloat() - level.random.nextFloat()) * 0.2f + 1.0f
            )
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true))
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)

            stack.shrink(1)
            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        } else if (stack.`is`(Items.FLINT_AND_STEEL) && canBeLit(state)) {
            // Taken from FLintAndSteelItem class
            level.playSound(
                player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f,
                level.getRandom().nextFloat() * 0.4f + 0.8f
            )
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 11)
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)

            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand))
            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    companion object {
        val SHAPE: VoxelShape = box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
    }
}