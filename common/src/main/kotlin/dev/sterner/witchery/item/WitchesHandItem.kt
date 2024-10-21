package dev.sterner.witchery.item

import dev.sterner.witchery.handler.InfusionHandler
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class WitchesHandItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        return InteractionResultHolder.consume(player.getItemInHand(usedHand))
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 72000
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        if (livingEntity is Player) {
            InfusionHandler.onHoldRightClick(livingEntity)
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration)
    }

    override fun releaseUsing(stack: ItemStack, level: Level, livingEntity: LivingEntity, timeCharged: Int) {
        super.releaseUsing(stack, level, livingEntity, timeCharged)

        if (livingEntity is Player) {
            val totalHeldTicks = getUseDuration(stack, livingEntity) - timeCharged

            if (totalHeldTicks >= 20) {
                InfusionHandler.onHoldReleaseRightClick(livingEntity, totalHeldTicks % 20)
            }
        }
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BLOCK
    }
}