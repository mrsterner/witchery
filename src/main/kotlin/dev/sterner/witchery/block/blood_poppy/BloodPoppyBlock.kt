package dev.sterner.witchery.block.blood_poppy

import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.FlowerBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult

class BloodPoppyBlock(effect: Holder<MobEffect>, duration: Float, properties: Properties) :
    FlowerBlock(effect, duration, properties), EntityBlock {

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(HAS_TAGLOCK, false))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BloodPoppyBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder.add(HAS_TAGLOCK))
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        val be = level.getBlockEntity(pos)
        if (!level.isClientSide && entity is LivingEntity && be is BloodPoppyBlockEntity) {
            be.uuid = entity.uuid
            be.setChanged()
            entity.hurt(entity.damageSources().cactus(), 1.0f)
            level.setBlockAndUpdate(pos, state.setValue(HAS_TAGLOCK, true))
        }

        super.entityInside(state, level, pos, entity)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val be = level.getBlockEntity(pos)

        if (hand == InteractionHand.OFF_HAND) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        }

        if (!stack.`is`(Items.GLASS_BOTTLE) || be !is BloodPoppyBlockEntity || be.uuid == null) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        }

        if (level !is ServerLevel) {
            return ItemInteractionResult.SUCCESS
        }


        val entity = be.uuid?.let { level.getEntity(it) }
        if (entity == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        }
        be.uuid = null
        be.setChanged()

        if (entity !is LivingEntity) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        }

        level.setBlockAndUpdate(pos, state.setValue(HAS_TAGLOCK, false))

        val taglock = ItemStack(WitcheryItems.TAGLOCK.get())
        TaglockItem.bindPlayerOrLiving(entity, taglock)

        if (player.addItem(taglock)) {
            stack.shrink(1)
        }

        return ItemInteractionResult.SUCCESS
    }

    companion object {
        val HAS_TAGLOCK: BooleanProperty = BooleanProperty.create("has_taglock")
    }
}