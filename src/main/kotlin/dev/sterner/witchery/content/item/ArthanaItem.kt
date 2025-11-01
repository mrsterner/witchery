package dev.sterner.witchery.content.item

import dev.sterner.witchery.content.block.arthana.ArthanaBlockEntity
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.Direction
import net.minecraft.tags.EntityTypeTags
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.animal.Chicken
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.context.UseOnContext

class ArthanaItem(properties: Properties) : SwordItem(Tiers.GOLD, properties) {

    override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        if (target is Chicken && attacker is Player) {
            target.hurt(attacker.damageSources().playerAttack(attacker), 4f)
        }
        if (target.isDeadOrDying && target.type.`is`(EntityTypeTags.UNDEAD) && attacker is Player) {
            if (attacker.level().random.nextFloat() < 0.15f) {
                val spectralDust = ItemStack(WitcheryItems.SPECTRAL_DUST.get())
                target.spawnAtLocation(spectralDust)
            }
        }
        return super.hurtEnemy(stack, target, attacker)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val face = context.clickedFace
        val state = level.getBlockState(pos)
        if ((state.`is`(WitcheryBlocks.ALTAR.get()) || state.`is`(WitcheryBlocks.ALTAR_COMPONENT.get())) && face == Direction.UP) {
            if (!level.isClientSide) {
                val newState = WitcheryBlocks.ARTHANA.get().defaultBlockState()
                level.setBlockAndUpdate(pos.above(), newState)

                val be = ArthanaBlockEntity(pos.above(), newState)
                be.arthana = context.itemInHand.copy()
                context.itemInHand.shrink(1)
                level.setBlockEntity(be)
            }
            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }
}