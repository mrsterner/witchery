package dev.sterner.witchery.item.potion

import dev.sterner.witchery.entity.WitcheryThrownPotion
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class WitcheryPotionItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        val ingredients = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return InteractionResultHolder.pass(stack)

        val lastType = ingredients.lastOrNull()?.type ?: return InteractionResultHolder.pass(stack)

        return when (lastType) {
            WitcheryPotionIngredient.Type.DRINK -> {
                player.startUsingItem(hand)
                InteractionResultHolder.consume(stack)
            }

            WitcheryPotionIngredient.Type.SPLASH -> {
                if (!level.isClientSide) {
                    val thrown = WitcheryThrownPotion(level, player)
                    thrown.item = stack
                    thrown.shootFromRotation(player, player.xRot, player.yRot, 0f, 1.5f, 1.0f)
                    level.addFreshEntity(thrown)
                    if (!player.abilities.instabuild) {
                        stack.shrink(1)
                    }
                }
                InteractionResultHolder.sidedSuccess(stack, level.isClientSide)
            }

            WitcheryPotionIngredient.Type.LINGERING -> {
                // TODO: Handle lingering potion throw (e.g. create AreaEffectCloud)
                InteractionResultHolder.sidedSuccess(stack, level.isClientSide)
            }
        }
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int = 32


    override fun getUseAnimation(stack: ItemStack): UseAnim {
        val ingredients = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return UseAnim.NONE
        val lastType = ingredients.lastOrNull()?.type ?: return UseAnim.NONE
        return if (lastType == WitcheryPotionIngredient.Type.DRINK) UseAnim.DRINK else UseAnim.NONE
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (entity is Player && !entity.abilities.instabuild) {
            stack.shrink(1)
        }

        // Apply effects based on ingredients...

        return stack
    }
}
